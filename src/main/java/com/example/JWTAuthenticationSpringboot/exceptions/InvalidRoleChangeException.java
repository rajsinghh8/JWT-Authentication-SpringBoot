package com.example.JWTAuthenticationSpringboot.exceptions;

// Thrown when an admin attempts an unsupported role change, e.g. changing
// their own role. Mapped to HTTP 400 by GlobalExceptionHandler.
public class InvalidRoleChangeException extends RuntimeException {
    public InvalidRoleChangeException(String message) {
        super(message);
    }
}
