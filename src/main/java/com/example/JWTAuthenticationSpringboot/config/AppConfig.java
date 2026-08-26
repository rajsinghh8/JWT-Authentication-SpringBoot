package com.example.JWTAuthenticationSpringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Note: the UserDetailsService bean is no longer defined here. It is provided
// by UserService (com.example.JWTAuthenticationSpringboot.services.UserService),
// which loads/persists users from the Oracle-backed app_users table instead of
// an in-memory user store. Spring Security auto-wires the single
// UserDetailsService + PasswordEncoder bean pair below into the
// AuthenticationManager.
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}
