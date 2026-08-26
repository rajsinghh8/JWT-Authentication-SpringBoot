package com.example.JWTAuthenticationSpringboot.exceptions;

// Thrown by UserService#registerUser when the requested email already
// belongs to an existing user. Mapped to HTTP 409 by GlobalExceptionHandler.
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("A user with email '" + email + "' already exists");
    }
}
