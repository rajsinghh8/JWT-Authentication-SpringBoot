package com.example.JWTAuthenticationSpringboot.services;

import com.example.JWTAuthenticationSpringboot.exceptions.DuplicateEmailException;
import com.example.JWTAuthenticationSpringboot.exceptions.InvalidRoleChangeException;
import com.example.JWTAuthenticationSpringboot.exceptions.UserNotFoundException;
import com.example.JWTAuthenticationSpringboot.models.RegisterRequest;
import com.example.JWTAuthenticationSpringboot.models.Role;
import com.example.JWTAuthenticationSpringboot.models.User;
import com.example.JWTAuthenticationSpringboot.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

// Backs both the /home user listing endpoints and Spring Security
// authentication: users are persisted in / loaded from the Oracle-backed
// app_users table via UserRepository instead of an in-memory store.
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Seed the (now database-backed) users table with the original demo
    // users the first time the application starts against an empty database.
    // The first seeded account is granted ADMIN so there is always a way to
    // log in and exercise the admin-only role-management endpoints.
    @PostConstruct
    public void seed() {
        if (userRepository.count() == 0) {
            userRepository.save(new User(UUID.randomUUID().toString(), "Prathiksha Kini",
                    "gpkini2002@gmail.com", passwordEncoder.encode("abc"), Role.ADMIN));
            userRepository.save(new User(UUID.randomUUID().toString(), "Padmini Kini",
                    "kinipadmini@gmail.com", passwordEncoder.encode("123"), Role.USER));
            userRepository.save(new User(UUID.randomUUID().toString(), "Mahalasa Kini",
                    "kinimahalasa@gmail.com", passwordEncoder.encode("password"), Role.USER));
            userRepository.save(new User(UUID.randomUUID().toString(), "Gurudath Kini",
                    "gurukini@gmail.com", passwordEncoder.encode("password"), Role.USER));
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Registers a brand new user. Emails act as usernames in this app, so
    // duplicates are rejected. New users always get the USER role - callers
    // cannot elevate themselves to ADMIN via registration.
    public User registerUser(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = new User(
                UUID.randomUUID().toString(),
                request.getName(),
                email,
                passwordEncoder.encode(request.getPassword()),
                Role.USER);

        return userRepository.save(user);
    }

    // Admin-only: change another user's role. Admins may not change their
    // own role through this endpoint.
    public User updateUserRole(String userId, Role newRole, String actingAdminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getEmail().equalsIgnoreCase(actingAdminEmail)) {
            throw new InvalidRoleChangeException("Admins cannot change their own role");
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }

    // Loads the authenticating user (matched by email) from Oracle so that
    // AuthController#login / JWTAuthenticationFilter authenticate and validate
    // tokens against real, database-persisted users. The user's role is
    // translated into a "ROLE_x" authority so Spring Security's hasRole()/
    // path-based authorization (see SecurityConfig) can enforce it.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email " + email));

        Role role = user.getRole() != null ? user.getRole() : Role.USER;

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name())))
                .build();
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT);
    }
}
