package com.example.JWTAuthenticationSpringboot.services;

import com.example.JWTAuthenticationSpringboot.models.User;
import com.example.JWTAuthenticationSpringboot.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Seed the (now database-backed) users table with the original demo
    // users the first time the application starts against an empty database.
    @PostConstruct
    public void seed() {
        if (userRepository.count() == 0) {
            userRepository.save(new User(UUID.randomUUID().toString(), "Prathiksha Kini",
                    "gpkini2002@gmail.com"));
            userRepository.save(new User(UUID.randomUUID().toString(), "Padmini Kini",
                    "kinipadmini@gmail.com"));
            userRepository.save(new User(UUID.randomUUID().toString(), "Mahalasa Kini",
                    "kinimahalasa@gmail.com"));
            userRepository.save(new User(UUID.randomUUID().toString(), "Gurudath Kini",
                    "gurukini@gmail.com"));
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
