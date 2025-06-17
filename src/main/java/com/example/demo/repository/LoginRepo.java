package com.example.demo.repository;

import com.example.demo.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepo extends JpaRepository<Login, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}