package com.healthcare.hospital.dto;

import lombok.Data;

@Data
public class UpdateHospitalRequest {
    private String name;
    private String address;
    private String phone;
    private String email;
}