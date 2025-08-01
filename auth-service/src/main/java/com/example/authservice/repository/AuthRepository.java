package com.example.authservice.repository;

import com.example.authservice.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, String> {
    Optional<Auth> findByUsernameAndIsDeletedFalse(String username);
}
