package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.*;
import com.healthcare.hospital.entity.Hospital;
import com.healthcare.hospital.repository.HospitalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalService {

    private final HospitalRepository hospitalRepo;
    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Transactional
    public HospitalResponse createHospital(CreateHospitalRequest request, String superAdminToken) {
        // 1. Create the HOSPITAL_ADMIN user in auth-service
        UUID adminUserId = createAdminUser(request.getAdminEmail(), request.getAdminPassword(), superAdminToken);

        // 2. Save the hospital with the admin user id
        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .adminUserId(adminUserId)
                .build();

        hospital = hospitalRepo.save(hospital);
        log.info("Hospital created: id={}, name={}, adminUserId={}", hospital.getId(), hospital.getName(), adminUserId);
        return HospitalResponse.from(hospital);
    }

    private UUID createAdminUser(String email, String password, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, String> body = Map.of(
                "email", email,
                "password", password,
                "role", "HOSPITAL_ADMIN"
        );

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                authServiceUrl + "/auth/admin/create-user", requestEntity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to create hospital admin user");
        }

        return UUID.fromString(response.getBody().get("userId").toString());
    }

    public List<HospitalResponse> getAllActiveHospitals() {
        return hospitalRepo.findAll().stream()
                .filter(Hospital::isActive)
                .map(HospitalResponse::from)
                .collect(Collectors.toList());
    }

    public HospitalResponse getHospitalById(UUID hospitalId) {
        Hospital hospital = hospitalRepo.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));
        return HospitalResponse.from(hospital);
    }

    public HospitalResponse getHospitalByAdminUserId(UUID adminUserId) {
        Hospital hospital = hospitalRepo.findByAdminUserId(adminUserId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found for this admin"));
        return HospitalResponse.from(hospital);
    }

    @Transactional
    public HospitalResponse updateHospital(UUID hospitalId, UpdateHospitalRequest request) {
        Hospital hospital = hospitalRepo.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));
        if (request.getName() != null) hospital.setName(request.getName());
        if (request.getAddress() != null) hospital.setAddress(request.getAddress());
        if (request.getPhone() != null) hospital.setPhone(request.getPhone());
        if (request.getEmail() != null) hospital.setEmail(request.getEmail());
        hospital = hospitalRepo.save(hospital);
        return HospitalResponse.from(hospital);
    }

    // Placeholder methods – will be replaced by REST calls to doctor/patient services later
    public List<?> getDoctorsByHospital(UUID hospitalId) {
        // TODO: call doctor-service
        return List.of();
    }

    public List<?> getPatientsByHospital(UUID hospitalId) {
        // TODO: call patient-service
        return List.of();
    }
}