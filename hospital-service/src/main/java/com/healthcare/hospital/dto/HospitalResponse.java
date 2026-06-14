package com.healthcare.hospital.dto;

import com.healthcare.hospital.entity.Hospital;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class HospitalResponse {
    private UUID id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private UUID adminUserId;
    private boolean active;

    public static HospitalResponse from(Hospital h) {
        return HospitalResponse.builder()
                .id(h.getId())
                .name(h.getName())
                .address(h.getAddress())
                .phone(h.getPhone())
                .email(h.getEmail())
                .adminUserId(h.getAdminUserId())
                .active(h.isActive())
                .build();
    }
}