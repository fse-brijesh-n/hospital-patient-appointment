package com.healthcare.auth.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String SECRET =
            "mySecretKeyForJwtTestingMustBeAtLeast32CharactersLong";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                SECRET,
                3600000L,      // 1 hour
                86400000L      // 1 day
        );
    }

    @Test
    @DisplayName("Should generate access token")
    void shouldGenerateAccessToken() {
        UUID userId = UUID.randomUUID();

        String token = jwtUtil.generateAccessToken(
                userId,
                "test@example.com",
                "PATIENT"
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Should generate refresh token")
    void shouldGenerateRefreshToken() {
        UUID userId = UUID.randomUUID();

        String token = jwtUtil.generateRefreshToken(userId);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Should parse access token correctly")
    void shouldParseToken() {
        UUID userId = UUID.randomUUID();

        String token = jwtUtil.generateAccessToken(
                userId,
                "test@example.com",
                "PATIENT"
        );

        Claims claims = jwtUtil.parseToken(token);

        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(
                "test@example.com",
                claims.get("email", String.class)
        );
        assertEquals(
                "PATIENT",
                claims.get("role", String.class)
        );
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        UUID userId = UUID.randomUUID();

        String token = jwtUtil.generateAccessToken(
                userId,
                "test@example.com",
                "PATIENT"
        );

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    @DisplayName("Should reject invalid token")
    void shouldRejectInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() throws InterruptedException {

        JwtUtil shortLivedJwtUtil = new JwtUtil(
                SECRET,
                1L,
                1L
        );

        String token = shortLivedJwtUtil.generateAccessToken(
                UUID.randomUUID(),
                "test@example.com",
                "PATIENT"
        );

        Thread.sleep(10);

        assertFalse(shortLivedJwtUtil.isTokenValid(token));
    }
}