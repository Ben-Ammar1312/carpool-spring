package gle.carpoolspring.service;

import gle.carpoolspring.enums.AvisType;
import gle.carpoolspring.model.Avis;
import gle.carpoolspring.model.Conducteur;
import gle.carpoolspring.model.Passager;
import gle.carpoolspring.repository.AvisRepository;
import gle.carpoolspring.repository.ConducteurRepository;
import gle.carpoolspring.repository.PassagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
public class AvisService {

    @Autowired
    private AvisRepository avisRepository;

    @Autowired
    private ConducteurRepository conducteurRepository;

    @Autowired
    private PassagerRepository passagerRepository;

    public Avis saveAvis(int conducteurId, int passagerId, float rating, AvisType type) throws Exception {
        Conducteur conducteur = conducteurRepository.findById(conducteurId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Passager passager = passagerRepository.findById(passagerId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Avis avis = new Avis();
        avis.setNote(rating);
        avis.setConducteur(conducteur);
        avis.setPassager(passager);
        avis.setAvisType(type);
        // Save the new rating
        avis = avisRepository.save(avis);
        // Recompute the average rating for either the driver or passenger
        if (type == AvisType.PASSENGER_TO_DRIVER) {
            float newAvg = avisRepository.findAverageRatingForDriver(conducteurId);
            conducteur.setNote(newAvg);
            conducteurRepository.save(conducteur);
        } else if (type == AvisType.DRIVER_TO_PASSENGER) {
            float newAvg = avisRepository.findAverageRatingForPassenger(passagerId);
            passager.setNote(newAvg);
            passagerRepository.save(passager);
        }


        return avis;
    }
}