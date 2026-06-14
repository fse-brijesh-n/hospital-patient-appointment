package com.healthcare.auth.repository;

import com.healthcare.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password_hash("hashedPassword123")
                .role(User.Role.PATIENT)
                .refreshToken("refreshToken123")
                .build();
    }

    @Test
    @DisplayName("Should save a User entity successfully")
    void testSaveUser() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getPassword_hash(), savedUser.getPassword_hash());
        assertEquals(testUser.getRole(), savedUser.getRole());
    }

    @Test
    @DisplayName("Should find User by ID")
    void testFindUserById() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser =
                userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when User ID not found")
    void testFindUserByIdNotFound() {
        Optional<User> foundUser =
                userRepository.findById(java.util.UUID.randomUUID());

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find User by email")
    void testFindUserByEmail() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser =
                userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    @Test
    @DisplayName("Should return empty Optional when email not found")
    void testFindUserByEmailNotFound() {
        Optional<User> foundUser =
                userRepository.findByEmail("notfound@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find User by refresh token")
    void testFindUserByRefreshToken() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser =
                userRepository.findByRefreshToken("refreshToken123");

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    @Test
    @DisplayName("Should return empty Optional when refresh token not found")
    void testFindUserByRefreshTokenNotFound() {
        Optional<User> foundUser =
                userRepository.findByRefreshToken("invalid-token");

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should check if email exists")
    void testExistsByEmail() {
        userRepository.save(testUser);

        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("unknown@example.com"));
    }

    @Test
    @DisplayName("Should update User entity")
    void testUpdateUser() {
        User savedUser = userRepository.save(testUser);

        savedUser.setEmail("updated@example.com");
        savedUser.setPassword_hash("newHashedPassword");
        savedUser.setRole(User.Role.DOCTOR);

        User updatedUser = userRepository.save(savedUser);

        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("newHashedPassword", updatedUser.getPassword_hash());
        assertEquals(User.Role.DOCTOR, updatedUser.getRole());
    }

    @Test
    @DisplayName("Should delete User by ID")
    void testDeleteUserById() {
        User savedUser = userRepository.save(testUser);

        userRepository.deleteById(savedUser.getId());

        Optional<User> deletedUser =
                userRepository.findById(savedUser.getId());

        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should delete User entity")
    void testDeleteUser() {
        User savedUser = userRepository.save(testUser);

        userRepository.delete(savedUser);

        Optional<User> deletedUser =
                userRepository.findById(savedUser.getId());

        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should count users")
    void testUserCount() {
        long before = userRepository.count();

        userRepository.save(
                User.builder()
                        .email("count@example.com")
                        .password_hash("hash")
                        .role(User.Role.PATIENT)
                        .build()
        );

        long after = userRepository.count();

        assertEquals(before + 1, after);
    }

    @Test
    @DisplayName("Should save user with null refresh token")
    void testSaveUserWithNullRefreshToken() {
        User user = User.builder()
                .email("nulltoken@example.com")
                .password_hash("hash")
                .role(User.Role.PATIENT)
                .build();

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertNull(savedUser.getRefreshToken());
    }

    @Test
    @DisplayName("Should save user with all roles")
    void testSaveUserWithEachRole() {
        for (User.Role role : User.Role.values()) {

            User user = User.builder()
                    .email(role.name().toLowerCase() + "@example.com")
                    .password_hash("hash")
                    .role(role)
                    .build();

            User savedUser = userRepository.save(user);

            Optional<User> retrieved =
                    userRepository.findById(savedUser.getId());

            assertTrue(retrieved.isPresent());
            assertEquals(role, retrieved.get().getRole());
        }
    }

    @Test
    @DisplayName("Should save timestamps automatically")
    void testSaveUserWithTimestamps() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }
}