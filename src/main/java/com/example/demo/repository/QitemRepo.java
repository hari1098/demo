package com.example.demo.repository;

import com.example.demo.model.Qitem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QitemRepo extends JpaRepository<Qitem,Integer> {
}
