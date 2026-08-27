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
    // Opaque, database-persisted token (see RefreshTokenService) clients can
    // exchange at /auth/refresh-token for a new jwtToken once the access
    // token expires, without re-submitting credentials.
    private String refreshToken;
    private String username;
    // Included so clients can immediately know the authenticated/registered
    // user's role without decoding the JWT.
    private Role role;
}
