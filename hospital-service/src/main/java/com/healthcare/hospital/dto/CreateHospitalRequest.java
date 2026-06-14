package com.healthcare.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateHospitalRequest {

    @NotBlank
    private String name;

    private String address;
    private String phone;

    @Email
    private String email;           // hospital contact email

    @NotBlank @Email
    private String adminEmail;

    @NotBlank @Size(min = 6)
    private String adminPassword;
}