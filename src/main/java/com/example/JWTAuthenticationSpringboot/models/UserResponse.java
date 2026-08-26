package com.example.JWTAuthenticationSpringboot.models;

import lombok.*;

// Safe, password-free view of a User returned from registration and
// admin user-management endpoints.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserResponse {
    private String userId;
    private String name;
    private String email;
    private Role role;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
