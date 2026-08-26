package com.example.JWTAuthenticationSpringboot.exceptions;

// Thrown when an admin operation references a userId that doesn't exist.
// Mapped to HTTP 404 by GlobalExceptionHandler.
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("No user found with id '" + userId + "'");
    }
}
