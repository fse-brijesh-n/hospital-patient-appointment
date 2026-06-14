package com.healthcare.auth.super_admin;

import com.healthcare.auth.entity.User;
import com.healthcare.auth.entity.User.Role;
import com.healthcare.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${super-admin.email}")
    private String adminEmail;

    @Value("${super-admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepo.existsByEmail(adminEmail)) {
            User superAdmin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.SUPER_ADMIN)
                    .build();
            userRepo.save(superAdmin);
            log.info("Super Admin seeded: {}", adminEmail);
        }
    }
}