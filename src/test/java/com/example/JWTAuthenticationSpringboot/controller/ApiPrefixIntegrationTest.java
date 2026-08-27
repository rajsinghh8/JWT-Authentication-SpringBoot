package com.example.JWTAuthenticationSpringboot.controller;

import com.example.JWTAuthenticationSpringboot.models.JwtRequest;
import com.example.JWTAuthenticationSpringboot.models.JwtResponse;
import com.example.JWTAuthenticationSpringboot.models.RegisterRequest;
import com.example.JWTAuthenticationSpringboot.repositories.RefreshTokenRepository;
import com.example.JWTAuthenticationSpringboot.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiPrefixIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String createdEmail;

    @AfterEach
    void removeCreatedUser() {
        if (createdEmail != null) {
            userRepository.findByEmailIgnoreCase(createdEmail).ifPresent(user -> {
                refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
                userRepository.delete(user);
            });
        }
    }

    @Test
    void loginAndAuthenticatedEndpointAreAvailableUnderApiV1() {
        createdEmail = "api-prefix-" + UUID.randomUUID() + "@example.com";
        String password = "password123";
        ResponseEntity<JwtResponse> registrationResponse = restTemplate.postForEntity(
                "/api/v1/auth/register",
                new RegisterRequest("API Prefix Test", createdEmail, password),
                JwtResponse.class);

        assertEquals(HttpStatus.CREATED, registrationResponse.getStatusCode());
        assertNotNull(registrationResponse.getBody());
        assertNotNull(registrationResponse.getBody().getJwtToken());

        ResponseEntity<JwtResponse> loginResponse = restTemplate.postForEntity(
                "/api/v1/auth/login",
                new JwtRequest(createdEmail, password),
                JwtResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertNotNull(loginResponse.getBody().getJwtToken());
        assertFalse(loginResponse.getBody().getJwtToken().isBlank());
        assertEquals(createdEmail, loginResponse.getBody().getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getJwtToken());
        ResponseEntity<String> currentUserResponse = restTemplate.exchange(
                "/api/v1/home/current-user",
                org.springframework.http.HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.OK, currentUserResponse.getStatusCode());
        assertEquals(createdEmail, currentUserResponse.getBody());
    }
}
