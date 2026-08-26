package com.example.JWTAuthenticationSpringboot.models;

// Application roles. New self-registered users always get USER; ADMIN is
// required to manage other users' roles (see AdminController).
public enum Role {
    USER,
    ADMIN
}
