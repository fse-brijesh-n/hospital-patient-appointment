package com.healthcare.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class VerifyResponse {
    private boolean valid;
    private UUID userId;
    private String email;
    private String role;
}