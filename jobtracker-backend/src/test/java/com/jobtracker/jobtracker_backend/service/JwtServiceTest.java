package com.jobtracker.jobtracker_backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-secret-key-that-is-long-enough-for-hs256-signing",
                3600000L
        );
    }

    @Test
    void generateToken_thenExtractUserId_returnsOriginalUserId() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "test@example.com");

        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
    }

    @Test
    void generateToken_thenExtractEmail_returnsOriginalEmail() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "test@example.com");

        assertThat(jwtService.extractEmail(token)).isEqualTo("test@example.com");
    }

    @Test
    void isTokenValid_withValidToken_returnsTrue() {
        String token = jwtService.generateToken(UUID.randomUUID(), "test@example.com");

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_withGarbageToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("not-a-real-token")).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_returnsFalse() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService(
                "test-secret-key-that-is-long-enough-for-hs256-signing",
                1L
        );
        String token = shortLivedJwtService.generateToken(UUID.randomUUID(), "test@example.com");

        Thread.sleep(10);

        assertThat(shortLivedJwtService.isTokenValid(token)).isFalse();
    }
}
