package com.example.demo.service;


import com.example.demo.model.Qitem;
import com.example.demo.repository.QitemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QitemService {

    @Autowired
    QitemRepo qitemRepo;


    public List<Qitem> getQitems() {
        return qitemRepo.findAll();
    }

    public Optional<Qitem> getQitemById(int id) {
        return qitemRepo.findById(id);
    }

    public Qitem createQitem(Qitem qitem) {
        return qitemRepo.save(qitem);
    }


    public Qitem updateQitem(int id, Qitem updatedqitem) {
        return qitemRepo.findById(id).map(qitem -> {
            qitem.setId(updatedqitem.getId());
            qitem.setQitem(updatedqitem.getQitem());
            qitem.setItemId(updatedqitem.getItemId());
            qitem.setQty(updatedqitem.getQty());
            qitem.setLicenseType(updatedqitem.getLicenseType());
            return qitemRepo.save(qitem);
        }).orElse(updatedqitem);
    }

    public boolean deleteQitem(int id) {
        qitemRepo.deleteById(id);
        return false;
    }
}
