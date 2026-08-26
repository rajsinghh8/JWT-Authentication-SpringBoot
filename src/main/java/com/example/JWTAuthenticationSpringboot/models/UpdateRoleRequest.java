package com.example.JWTAuthenticationSpringboot.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;

// Body for PUT /admin/users/{userId}/role — admin-only role management.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateRoleRequest {
    @NotNull(message = "role is required")
    private Role role;
}
