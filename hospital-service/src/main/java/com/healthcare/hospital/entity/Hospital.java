package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "hospitals", schema = "hospital_service")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;
    private String email;

    @Column(name = "admin_user_id", nullable = false, unique = true)
    private UUID adminUserId;

    @Builder.Default
    @Column(name = "is_active")
    private boolean active = true;
}