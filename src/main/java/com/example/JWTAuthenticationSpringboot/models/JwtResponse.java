package com.example.JWTAuthenticationSpringboot.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JwtResponse {
    private String jwtToken;
    private String username;
    // Included so clients can immediately know the authenticated/registered
    // user's role without decoding the JWT.
    private Role role;
}
