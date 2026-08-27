package com.example.JWTAuthenticationSpringboot.repositories;

import com.example.JWTAuthenticationSpringboot.models.RefreshToken;
import com.example.JWTAuthenticationSpringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    // Used to validate/rotate a refresh token presented to /auth/refresh-token.
    Optional<RefreshToken> findByToken(String token);

    // Used to enforce a single active refresh token per user (see
    // RefreshTokenService#createRefreshToken) and to revoke it on logout.
    Optional<RefreshToken> findByUser(User user);
}
