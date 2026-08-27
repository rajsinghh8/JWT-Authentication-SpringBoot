package com.example.JWTAuthenticationSpringboot.service;

import com.example.JWTAuthenticationSpringboot.exceptions.DuplicateEmailException;
import com.example.JWTAuthenticationSpringboot.models.RegisterRequest;
import com.example.JWTAuthenticationSpringboot.models.Role;
import com.example.JWTAuthenticationSpringboot.models.User;
import com.example.JWTAuthenticationSpringboot.repositories.UserRepository;
import com.example.JWTAuthenticationSpringboot.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUserNormalizesEmailAndEncodesPassword() {
        RegisterRequest request = new RegisterRequest("Test User", "Test.User@Example.COM", "password123");
        when(userRepository.existsByEmailIgnoreCase("test.user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("test.user@example.com", saved.getEmail());
        assertEquals("encoded-password", saved.getPassword());
        assertEquals(Role.USER, saved.getRole());
        assertEquals("test.user@example.com", userCaptor.getValue().getEmail());
    }

    @Test
    void registerUserRejectsDuplicateEmailIgnoringCase() {
        RegisterRequest request = new RegisterRequest("Test User", "Test.User@Example.COM", "password123");
        when(userRepository.existsByEmailIgnoreCase(eq("test.user@example.com"))).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.registerUser(request));

        verify(userRepository, never()).save(any(User.class));
    }
}
