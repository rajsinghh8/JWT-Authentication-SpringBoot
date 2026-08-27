package com.example.JWTAuthenticationSpringboot.config;

import com.example.JWTAuthenticationSpringboot.security.JWTAccessDeniedHandler;
import com.example.JWTAuthenticationSpringboot.security.JWTAthenticationEntryPoint;
import com.example.JWTAuthenticationSpringboot.security.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JWTAthenticationEntryPoint point;
    @Autowired
    private JWTAuthenticationFilter filter;
    @Autowired
    private JWTAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // configuration
        http.csrf(csrf->csrf.disable())
                .cors(cors->cors.disable())
                .authorizeHttpRequests(auth->auth
                        // Refresh/logout must be reachable without a (possibly expired)
                        // access token - they carry their own refresh-token credential.
                        .requestMatchers("/auth/login", "/auth/register", "/auth/refresh-token", "/auth/logout").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // User listings, including the legacy /home/user endpoint,
                        // contain account information and are admin-only.
                        .requestMatchers("/admin/**", "/home/user").hasRole("ADMIN")
                        .requestMatchers("/home/**").authenticated()
                        // Let Spring MVC produce a meaningful 404 for paths that do not
                        // exist instead of treating an unknown route as a protected API.
                        .anyRequest()
                        .permitAll())
                        .exceptionHandling(ex->ex.authenticationEntryPoint(point)
                                .accessDeniedHandler(accessDeniedHandler))
                        .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
