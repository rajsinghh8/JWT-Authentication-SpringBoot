package com.example.JWTAuthenticationSpringboot.services;

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
    @PostConstruct
    public void seed() {
        if (userRepository.count() == 0) {
            userRepository.save(new User(UUID.randomUUID().toString(), "Prathiksha Kini",
                    "gpkini2002@gmail.com", passwordEncoder.encode("abc")));
            userRepository.save(new User(UUID.randomUUID().toString(), "Padmini Kini",
                    "kinipadmini@gmail.com", passwordEncoder.encode("123")));
            userRepository.save(new User(UUID.randomUUID().toString(), "Mahalasa Kini",
                    "kinimahalasa@gmail.com", passwordEncoder.encode("password")));
            userRepository.save(new User(UUID.randomUUID().toString(), "Gurudath Kini",
                    "gurukini@gmail.com", passwordEncoder.encode("password")));
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Loads the authenticating user (matched by email) from Oracle so that
    // AuthController#login / JWTAuthenticationFilter authenticate and validate
    // tokens against real, database-persisted users.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}
