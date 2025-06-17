package com.example.demo.service;

import com.example.demo.model.Login;
import com.example.demo.repository.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private LoginRepo loginRepo;

    public List<Login> getAllLogins() {
        return loginRepo.findAll();
    }

    public Optional<Login> getloginById(int id) {
        return loginRepo.findById(id);
    }

    // ✅ Validation method separated
    public String validateLogin(Login login) {
        if (loginRepo.existsByEmail(login.getEmail())) {
            return "Email already exists";
        }
        if (loginRepo.existsByUsername(login.getUsername())) {
            return "Username already exists";
        }
        return "OK";
    }

    // ✅ Main create method
    public Login createLogin(Login login) {
        return loginRepo.save(login);
    }

    public Login updateLogin(int id, Login updatedLogin) {
        return loginRepo.findById(id).map(login -> {
            login.setUsername(updatedLogin.getUsername());
            login.setPassword(updatedLogin.getPassword());
            login.setEmail(updatedLogin.getEmail());
            login.setFirstName(updatedLogin.getFirstName());
            login.setLastName(updatedLogin.getLastName());
            login.setUserType(updatedLogin.getUserType());
            login.setCreatedBy(updatedLogin.getCreatedBy());
            login.setCreatedOn(updatedLogin.getCreatedOn());
            login.setUpdatedBy(updatedLogin.getUpdatedBy());
            login.setUpdatedOn(updatedLogin.getUpdatedOn());
            login.setIsActive(updatedLogin.getIsActive());
            return loginRepo.save(login);
        }).orElse(null);
    }

    public boolean deleteLogin(int id) {
        Optional<Login> login = loginRepo.findById(id);
        if (login.isPresent()) {
            loginRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
