package com.example.JWTAuthenticationSpringboot.config;

import com.example.JWTAuthenticationSpringboot.models.JwtRequest;
import com.example.JWTAuthenticationSpringboot.models.JwtResponse;
import com.example.JWTAuthenticationSpringboot.models.RefreshToken;
import com.example.JWTAuthenticationSpringboot.models.RefreshTokenRequest;
import com.example.JWTAuthenticationSpringboot.models.RegisterRequest;
import com.example.JWTAuthenticationSpringboot.models.Role;
import com.example.JWTAuthenticationSpringboot.models.User;
import com.example.JWTAuthenticationSpringboot.exceptions.TokenRefreshException;
import com.example.JWTAuthenticationSpringboot.security.JWTHelper;
import com.example.JWTAuthenticationSpringboot.services.RefreshTokenService;
import com.example.JWTAuthenticationSpringboot.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;


    @Autowired
    private JWTHelper helper;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest request) {

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .role(extractRole(userDetails))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Self-service registration. Always creates the new account with the
    // USER role (see UserService#registerUser) - registrants cannot grant
    // themselves ADMIN. Duplicate emails/usernames are rejected with 409 by
    // GlobalExceptionHandler; invalid payloads are rejected with 400.
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        User created = userService.registerUser(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(created.getEmail());
        String token = this.helper.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(created.getEmail());

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .refreshToken(refreshToken.getToken())
                .username(created.getEmail())
                .role(created.getRole())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Exchanges a valid, unexpired refresh token for a brand new access
    // token. The refresh token itself is rotated (the old one is invalidated
    // and a new one issued) so a stolen/replayed refresh token has a single
    // use window. Unknown or expired tokens raise TokenRefreshException,
    // mapped to HTTP 401 by GlobalExceptionHandler.
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not recognized. Please sign in again."));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = this.helper.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        JwtResponse response = JwtResponse.builder()
                .jwtToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .username(userDetails.getUsername())
                .role(extractRole(userDetails))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Revokes the given refresh token so it can no longer be redeemed for
    // new access tokens (the already-issued access token remains valid
    // until its own, short, expiration - it is stateless and not tracked).
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return new ResponseEntity<>(Collections.singletonMap("message", "Logged out successfully"), HttpStatus.OK);
    }

    private Role extractRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .map(Role::valueOf)
                .orElse(Role.USER);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, password);
        manager.authenticate(authentication);
    }
}
