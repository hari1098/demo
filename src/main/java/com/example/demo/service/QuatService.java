package com.example.demo.service;

import com.example.demo.model.Login;
import com.example.demo.model.Quat;
import com.example.demo.repository.QuatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuatService {

    @Autowired
    private QuatRepo quatRepository;



    public List<Quat> getAllQuats() {
        return quatRepository.findAll();
    }

    public Optional<Quat> getQuatById(int id) {
        return quatRepository.findById(id);
    }

    public void deleteQuat(int id) {
        quatRepository.deleteById(id);
    }

    public Quat updateQuat(int id, Quat updatequat) {
        return quatRepository.findById(id).map(quat -> {
            quat.setQuatno(updatequat.getQuatno());
            quat.setQuatDate(updatequat.getQuatDate());
            quat.setCustomerId(updatequat.getCustomerId());
            quat.setUserId(updatequat.getUserId());
            quat.setValidity(updatequat.getValidity());
            quat.setId(updatequat.getId());
            return quatRepository.save(quat);
        }).orElse(null);
    }






        public Quat createQuat(Quat quat) {
            return quatRepository.save(quat);
        }



}
