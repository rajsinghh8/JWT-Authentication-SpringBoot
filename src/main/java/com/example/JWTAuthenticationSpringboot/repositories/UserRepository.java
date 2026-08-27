package com.example.JWTAuthenticationSpringboot.repositories;

import com.example.JWTAuthenticationSpringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Email addresses are usernames in this application and are matched
    // case-insensitively against the Oracle-backed app_users table.
    Optional<User> findByEmailIgnoreCase(String email);

    // Used by UserService#registerUser to reject duplicate usernames even
    // when the submitted email differs only by capitalization.
    boolean existsByEmailIgnoreCase(String email);
}
