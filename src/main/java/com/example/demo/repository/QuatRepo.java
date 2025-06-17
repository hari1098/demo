package com.example.demo.repository;

import com.example.demo.model.Quat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuatRepo extends JpaRepository<Quat,Integer> {
}
