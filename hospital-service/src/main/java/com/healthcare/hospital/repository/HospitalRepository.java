package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface HospitalRepository extends JpaRepository<Hospital, UUID> {
    Optional<Hospital> findByAdminUserId(UUID adminUserId);
    boolean existsByAdminUserId(UUID adminUserId);
}