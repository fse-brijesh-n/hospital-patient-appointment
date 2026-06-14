package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.*;
import com.healthcare.hospital.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public HospitalResponse createHospital(@Valid @RequestBody CreateHospitalRequest request,
                                           Authentication authentication) {
        // The raw JWT token is stored as credentials by the filter
        String token = authentication.getCredentials().toString();
        return hospitalService.createHospital(request, token);
    }

    @GetMapping
    public List<HospitalResponse> getAllHospitals() {
        return hospitalService.getAllActiveHospitals();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('HOSPITAL_ADMIN')")
    public HospitalResponse getMyHospital(Authentication authentication) {
        UUID adminUserId = UUID.fromString(authentication.getName()); // userId from JWT sub
        return hospitalService.getHospitalByAdminUserId(adminUserId);
    }

    @GetMapping("/{hospitalId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HOSPITAL_ADMIN')")
    public HospitalResponse getHospital(@PathVariable UUID hospitalId) {
        return hospitalService.getHospitalById(hospitalId);
    }

    @PutMapping("/{hospitalId}")
    @PreAuthorize("hasRole('HOSPITAL_ADMIN')")
    public HospitalResponse updateHospital(@PathVariable UUID hospitalId,
                                           @Valid @RequestBody UpdateHospitalRequest request) {
        return hospitalService.updateHospital(hospitalId, request);
    }

    @GetMapping("/{hospitalId}/doctors")
    @PreAuthorize("hasAnyRole('HOSPITAL_ADMIN','SUPER_ADMIN')")
    public List<?> getDoctors(@PathVariable UUID hospitalId) {
        return hospitalService.getDoctorsByHospital(hospitalId);
    }

    @GetMapping("/{hospitalId}/patients")
    @PreAuthorize("hasAnyRole('HOSPITAL_ADMIN','SUPER_ADMIN')")
    public List<?> getPatients(@PathVariable UUID hospitalId) {
        return hospitalService.getPatientsByHospital(hospitalId);
    }
}