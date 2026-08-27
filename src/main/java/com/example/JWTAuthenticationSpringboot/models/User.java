package com.example.JWTAuthenticationSpringboot.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class User {
    @Id
    private String userId;
    private String name;
    private String email;
    // BCrypt-encoded password used to authenticate against this Oracle-backed
    // user record (see UserService#loadUserByUsername).
    private String password;
    // USER or ADMIN. Persisted as its name (e.g. "ADMIN") rather than an
    // ordinal so the column stays readable/stable across enum changes.
    @Enumerated(EnumType.STRING)
    private Role role;
}
