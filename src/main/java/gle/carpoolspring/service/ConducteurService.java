package gle.carpoolspring.service;

import gle.carpoolspring.model.Conducteur;
import gle.carpoolspring.repository.ConducteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConducteurService {
    @Autowired
    private ConducteurRepository conducteurReository;

    public List<Conducteur> getAllConducteur() {
        return conducteurReository.findAll();
    }

}


