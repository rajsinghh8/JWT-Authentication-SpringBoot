package com.example.JWTAuthenticationSpringboot.exceptions;

// Thrown when a refresh token presented to /auth/refresh-token (or
// /auth/logout) is missing from the database, expired, or otherwise
// invalid. Mapped to HTTP 401 by GlobalExceptionHandler so callers know to
// re-authenticate via /auth/login.
public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {
        super(message);
    }
}
