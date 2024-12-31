package gle.carpoolspring.service;

import gle.carpoolspring.model.Reclamation;
import gle.carpoolspring.repository.ReclamationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReclamationService {

    @Autowired
    private ReclamationRepository reclamationRepository;

    public Reclamation findById(int idReclamation) {
        return reclamationRepository.findById(idReclamation).orElse(null); // Returns null if not found
    }

    public void save(Reclamation reclamation) {
        reclamationRepository.save(reclamation);
    }
}
