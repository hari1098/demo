package com.example.demo.repository;

import com.example.demo.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepo extends JpaRepository<Login, Integer> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
