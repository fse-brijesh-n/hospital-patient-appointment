package com.healthcare.auth.service;

import com.healthcare.auth.dto.*;
import com.healthcare.auth.entity.User;
import com.healthcare.auth.entity.User.Role;
import com.healthcare.auth.repository.UserRepository;
import com.healthcare.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse registerPatient(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.PATIENT)
                .build();

        return saveAndBuildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new SecurityException("Invalid email or password");
        }

        return buildAndSaveTokens(user);
    }

    @Transactional
    public AuthResponse createUserBySuperAdmin(CreateUserRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        if (role == Role.SUPER_ADMIN) {
            throw new SecurityException("Cannot create SUPER_ADMIN");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        AuthResponse response = saveAndBuildAuthResponse(user);
        log.info("Admin created user: {} with role {}", user.getEmail(), role);
        return response;
    }

    private AuthResponse saveAndBuildAuthResponse(User user) {
        user = userRepo.save(user);
        return buildAndSaveTokens(user);
    }

    private AuthResponse buildAndSaveTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        userRepo.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public VerifyResponse verifyToken(String token) {
        try {
            Claims claims = jwtUtil.parseToken(token);
            UUID userId = UUID.fromString(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            return VerifyResponse.builder()
                    .valid(true)
                    .userId(userId)
                    .email(email)
                    .role(role)
                    .build();
        } catch (Exception e) {
            return VerifyResponse.builder().valid(false).build();
        }
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new SecurityException("Invalid refresh token");
        }

        User user = userRepo.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SecurityException("Refresh token not found"));

        // Invalidate old refresh token and issue new pair
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
        user.setRefreshToken(newRefreshToken);
        userRepo.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        userRepo.findByRefreshToken(refreshToken).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepo.save(user);
        });
    }
}