package com.example.JWTAuthenticationSpringboot.services;

import com.example.JWTAuthenticationSpringboot.exceptions.TokenRefreshException;
import com.example.JWTAuthenticationSpringboot.models.RefreshToken;
import com.example.JWTAuthenticationSpringboot.models.User;
import com.example.JWTAuthenticationSpringboot.repositories.RefreshTokenRepository;
import com.example.JWTAuthenticationSpringboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// Handles generation, persistence (Oracle-backed via RefreshTokenRepository),
// validation and expiration of refresh tokens, decoupled from the (stateless,
// unpersisted) access-token JWTs handled by JWTHelper. This keeps the
// existing access-token/login flow untouched while adding a refresh flow on
// top of it.
@Service
public class RefreshTokenService {

    // Refresh token validity in milliseconds (default 7 days). Deliberately
    // much longer-lived than the access token (app.jwt.expiration-ms) so
    // clients can silently obtain new access tokens without re-authenticating.
    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // Issues a brand new refresh token for the given user (identified by
    // email/username), replacing any previously issued one so only a single
    // refresh token is valid per user at a time.
    @Transactional
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email " + email));

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // Ensures the token hasn't expired; expired tokens are deleted so they
    // can never be redeemed, forcing the client back through /auth/login.
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token has expired. Please sign in again.");
        }
        return token;
    }

    // Revokes a valid refresh token on logout. Unknown and expired tokens are
    // reported to the caller instead of returning a misleading success result.
    @Transactional
    public void deleteByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token,
                        "Refresh token is not recognized. Please sign in again."));
        verifyExpiration(refreshToken);
        refreshTokenRepository.delete(refreshToken);
    }
}
