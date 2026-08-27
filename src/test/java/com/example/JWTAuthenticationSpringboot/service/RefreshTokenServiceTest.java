package com.example.JWTAuthenticationSpringboot.service;

import com.example.JWTAuthenticationSpringboot.exceptions.TokenRefreshException;
import com.example.JWTAuthenticationSpringboot.models.RefreshToken;
import com.example.JWTAuthenticationSpringboot.repositories.RefreshTokenRepository;
import com.example.JWTAuthenticationSpringboot.repositories.UserRepository;
import com.example.JWTAuthenticationSpringboot.services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void verifyExpirationReturnsValidToken() {
        RefreshToken token = RefreshToken.builder()
                .token("active-token")
                .expiryDate(Instant.now().plusSeconds(60))
                .build();

        assertSame(token, refreshTokenService.verifyExpiration(token));
    }

    @Test
    void verifyExpirationDeletesAndRejectsExpiredToken() {
        RefreshToken token = RefreshToken.builder()
                .token("expired-token")
                .expiryDate(Instant.now().minusSeconds(60))
                .build();

        assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(token));

        verify(refreshTokenRepository).delete(token);
    }
}
