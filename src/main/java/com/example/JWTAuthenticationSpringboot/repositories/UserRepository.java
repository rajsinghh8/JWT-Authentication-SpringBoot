package com.example.JWTAuthenticationSpringboot.repositories;

import com.example.JWTAuthenticationSpringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Used by UserService (UserDetailsService) to look up the authenticating
    // user by email/username directly from the Oracle-backed app_users table.
    Optional<User> findByEmail(String email);

    // Used by UserService#registerUser to reject duplicate usernames (email
    // doubles as the username in this application).
    boolean existsByEmail(String email);
}
