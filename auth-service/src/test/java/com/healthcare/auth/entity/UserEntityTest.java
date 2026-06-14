package com.healthcare.auth.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserEntityTest {

    private User user;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        user = new User();
    }

    @Test
    @DisplayName("Should create User with all fields")
    void testCreateUserWithAllFields() {
        // Arrange
        String email = "test@example.com";
        String passwordHash = "hashedPassword123";
        User.Role role = User.Role.PATIENT;
        String refreshToken = "refreshToken123";

        // Act
        user.setId(testId);
        user.setEmail(email);
        user.setPassword_hash(passwordHash);
        user.setRole(role);
        user.setRefreshToken(refreshToken);

        // Assert
        assertNotNull(user.getId());
        assertEquals(testId, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPassword_hash());
        assertEquals(role, user.getRole());
        assertEquals(refreshToken, user.getRefreshToken());
    }

    @Test
    @DisplayName("Should create User using Builder pattern")
    void testCreateUserWithBuilder() {
        // Act
        LocalDateTime now = LocalDateTime.now();
        User builtUser = User.builder()
                .id(testId)
                .email("doctor@example.com")
                .password_hash("hashedPassword456")
                .role(User.Role.DOCTOR)
                .refreshToken("refreshToken456")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertNotNull(builtUser);
        assertEquals(testId, builtUser.getId());
        assertEquals("doctor@example.com", builtUser.getEmail());
        assertEquals("hashedPassword456", builtUser.getPassword_hash());
        assertEquals(User.Role.DOCTOR, builtUser.getRole());
        assertEquals("refreshToken456", builtUser.getRefreshToken());
    }

    @Test
    @DisplayName("Should create User with no arguments constructor")
    void testCreateUserWithNoArgsConstructor() {
        // Act
        User newUser = new User();

        // Assert
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getEmail());
        assertNull(newUser.getPassword_hash());
        assertNull(newUser.getRole());
        assertNull(newUser.getRefreshToken());
    }

    @Test
    @DisplayName("Should support User with PATIENT role")
    void testCreateUserWithPatientRole() {
        // Act
        user.setId(testId);
        user.setEmail("patient@example.com");
        user.setPassword_hash("patientPassword123");
        user.setRole(User.Role.PATIENT);

        // Assert
        assertEquals(User.Role.PATIENT, user.getRole());
    }

    @Test
    @DisplayName("Should support User with DOCTOR role")
    void testCreateUserWithDoctorRole() {
        // Act
        user.setId(testId);
        user.setEmail("doctor@example.com");
        user.setPassword_hash("doctorPassword123");
        user.setRole(User.Role.DOCTOR);

        // Assert
        assertEquals(User.Role.DOCTOR, user.getRole());
    }

    @Test
    @DisplayName("Should support User with HOSPITAL_ADMIN role")
    void testCreateUserWithHospitalAdminRole() {
        // Act
        user.setId(testId);
        user.setEmail("admin@hospital.com");
        user.setPassword_hash("adminPassword123");
        user.setRole(User.Role.HOSPITAL_ADMIN);

        // Assert
        assertEquals(User.Role.HOSPITAL_ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should support User with SUPER_ADMIN role")
    void testCreateUserWithSuperAdminRole() {
        // Act
        user.setId(testId);
        user.setEmail("superadmin@example.com");
        user.setPassword_hash("superAdminPassword123");
        user.setRole(User.Role.SUPER_ADMIN);

        // Assert
        assertEquals(User.Role.SUPER_ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should update User email")
    void testUpdateUserEmail() {
        // Arrange
        String initialEmail = "initial@example.com";
        String updatedEmail = "updated@example.com";
        user.setEmail(initialEmail);

        // Act
        user.setEmail(updatedEmail);

        // Assert
        assertEquals(updatedEmail, user.getEmail());
        assertNotEquals(initialEmail, user.getEmail());
    }

    @Test
    @DisplayName("Should update User password hash")
    void testUpdateUserPasswordHash() {
        // Arrange
        String initialHash = "initialHash123";
        String updatedHash = "updatedHash456";
        user.setPassword_hash(initialHash);

        // Act
        user.setPassword_hash(updatedHash);

        // Assert
        assertEquals(updatedHash, user.getPassword_hash());
        assertNotEquals(initialHash, user.getPassword_hash());
    }

    @Test
    @DisplayName("Should update User role")
    void testUpdateUserRole() {
        // Arrange
        user.setRole(User.Role.PATIENT);

        // Act
        user.setRole(User.Role.DOCTOR);

        // Assert
        assertEquals(User.Role.DOCTOR, user.getRole());
        assertNotEquals(User.Role.PATIENT, user.getRole());
    }

    @Test
    @DisplayName("Should update refresh token")
    void testUpdateRefreshToken() {
        // Arrange
        String initialToken = "token123";
        String updatedToken = "token456";
        user.setRefreshToken(initialToken);

        // Act
        user.setRefreshToken(updatedToken);

        // Assert
        assertEquals(updatedToken, user.getRefreshToken());
        assertNotEquals(initialToken, user.getRefreshToken());
    }

    @Test
    @DisplayName("Should generate unique UUIDs for different users")
    void testUserUniqueIdentifiers() {
        // Act
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        User user1 = new User();
        User user2 = new User();
        user1.setId(id1);
        user2.setId(id2);

        // Assert
        assertNotEquals(id1, id2);
        assertNotEquals(user1.getId(), user2.getId());
    }

    @Test
    @DisplayName("Should handle null values before assignment")
    void testUserNullHandling() {
        // Assert - Initial state
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword_hash());
        assertNull(user.getRole());
        assertNull(user.getRefreshToken());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should support timestamps for creation and updates")
    void testUserTimestamps() {
        // Arrange
        LocalDateTime createdTime = LocalDateTime.now();
        LocalDateTime updatedTime = LocalDateTime.now().plusMinutes(5);

        // Act
        user.setId(testId);
        user.setEmail("test@example.com");
        user.setPassword_hash("hashedPassword");
        user.setRole(User.Role.PATIENT);
        user.setCreatedAt(createdTime);
        user.setUpdatedAt(updatedTime);

        // Assert
        assertEquals(createdTime, user.getCreatedAt());
        assertEquals(updatedTime, user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(user.getCreatedAt()));
    }

    @Test
    @DisplayName("Should create User with all different roles")
    void testAllUserRolesCanBeAssigned() {
        // Arrange
        User.Role[] roles = User.Role.values();

        // Act & Assert
        for (User.Role role : roles) {
            User testUser = User.builder()
                    .id(UUID.randomUUID())
                    .email("test@example.com")
                    .password_hash("testHash")
                    .role(role)
                    .build();

            assertNotNull(testUser);
            assertEquals(role, testUser.getRole());
        }
    }

    @Test
    @DisplayName("Should support getting and setting all User fields")
    void testGettersAndSetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String passwordHash = "hashedPassword123";
        User.Role role = User.Role.DOCTOR;
        String refreshToken = "refreshToken123";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        user.setId(id);
        user.setEmail(email);
        user.setPassword_hash(passwordHash);
        user.setRole(role);
        user.setRefreshToken(refreshToken);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPassword_hash());
        assertEquals(role, user.getRole());
        assertEquals(refreshToken, user.getRefreshToken());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create User with complex email addresses")
    void testComplexEmailAddresses() {
        // Arrange & Act
        String[] complexEmails = {
            "user+tag@example.co.uk",
            "first.last@subdomain.example.com",
            "user_name@example-domain.org",
            "123@example.com"
        };

        // Assert
        for (String email : complexEmails) {
            User testUser = User.builder()
                    .id(UUID.randomUUID())
                    .email(email)
                    .password_hash("hash")
                    .role(User.Role.PATIENT)
                    .build();

            assertEquals(email, testUser.getEmail());
        }
    }

    @Test
    @DisplayName("Should create User with long password hash")
    void testLongPasswordHash() {
        // Arrange
        String longHash = "a".repeat(256);
        user.setPassword_hash(longHash);

        // Act & Assert
        assertEquals(longHash, user.getPassword_hash());
        assertEquals(256, user.getPassword_hash().length());
    }

    @Test
    @DisplayName("Should create User with all arguments constructor")
    void testCreateUserWithAllArgsConstructor() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String passwordHash = "hashedPassword";
        User.Role role = User.Role.DOCTOR;
        String refreshToken = "refreshToken";

        // Act
        User createdUser = new User(id, email, passwordHash, role, refreshToken, now, now);

        // Assert
        assertEquals(id, createdUser.getId());
        assertEquals(email, createdUser.getEmail());
        assertEquals(passwordHash, createdUser.getPassword_hash());
        assertEquals(role, createdUser.getRole());
        assertEquals(refreshToken, createdUser.getRefreshToken());
        assertEquals(now, createdUser.getCreatedAt());
        assertEquals(now, createdUser.getUpdatedAt());
    }
}
