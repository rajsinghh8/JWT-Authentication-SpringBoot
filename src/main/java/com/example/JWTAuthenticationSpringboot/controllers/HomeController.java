package com.example.JWTAuthenticationSpringboot.controllers;

import com.example.JWTAuthenticationSpringboot.models.UserResponse;
import com.example.JWTAuthenticationSpringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private UserService userService;

    // User listing is restricted to ADMIN in SecurityConfig. Return the
    // password-free response type so credential hashes are never serialized.
    @GetMapping("/user")
    public List<UserResponse> getUser(){
        return userService.getUsers().stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @GetMapping("/current-user")
    public String getLoggedInUser(Principal principal){
        return principal.getName();
    }
}
