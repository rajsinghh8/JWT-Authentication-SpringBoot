package com.example.JWTAuthenticationSpringboot.controllers;

import com.example.JWTAuthenticationSpringboot.models.UpdateRoleRequest;
import com.example.JWTAuthenticationSpringboot.models.User;
import com.example.JWTAuthenticationSpringboot.models.UserResponse;
import com.example.JWTAuthenticationSpringboot.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Admin-only user management. Access to "/admin/**" is restricted to
// ROLE_ADMIN in SecurityConfig, so every endpoint here already runs with an
// authenticated ADMIN principal.
@RestController
@RequestMapping("${app.api.prefix}/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // localhost:25330/admin/users
    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return userService.getUsers().stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    // Only an ADMIN (enforced by SecurityConfig) can change another user's
    // role. An admin changing their own role is rejected by UserService.
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable String userId,
                                                     @Valid @RequestBody UpdateRoleRequest request,
                                                     Authentication authentication) {
        User updated = userService.updateUserRole(userId, request.getRole(), authentication.getName());
        return ResponseEntity.ok(UserResponse.fromUser(updated));
    }
}
