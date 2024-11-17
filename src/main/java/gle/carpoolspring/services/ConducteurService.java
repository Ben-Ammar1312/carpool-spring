package gle.carpoolspring.services;

import gle.carpoolspring.models.Annonce;
import gle.carpoolspring.models.Conducteur;
import gle.carpoolspring.repositories.ConducteurReository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConducteurService {
    @Autowired
    private ConducteurReository conducteurReository;

    public List<Conducteur> getAllConducteur() {
        return conducteurReository.findAll();
    }

}


