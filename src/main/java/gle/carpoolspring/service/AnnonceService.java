package gle.carpoolspring.service;

import gle.carpoolspring.model.*;
import gle.carpoolspring.repository.AnnonceRepository;
import gle.carpoolspring.repository.PickPointRepository;
import gle.carpoolspring.repository.WaypointRepository;
import gle.carpoolspring.repository.WaypointSuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private PickPointRepository pickupPointRepository;

    @Autowired
    private WaypointSuggestionRepository waypointSuggestionRepository;

    @Autowired
    private WaypointRepository waypointRepository;




    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }


    public Annonce getAnnonceById(int id) {
        return annonceRepository.findById(id).orElse(null);
    }


    public void updateAnnonce(Annonce annonce) {
        Annonce existingAnnonce = annonceRepository.findById(annonce.getId_annonce())
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        existingAnnonce.setLieuDepart(annonce.getLieuDepart());
        existingAnnonce.setLieuArrivee(annonce.getLieuArrivee());
        existingAnnonce.setDateDepart(annonce.getDateDepart());
        existingAnnonce.setHeureDepart(annonce.getHeureDepart());
        existingAnnonce.setNbrPlaces(annonce.getNbrPlaces());
        existingAnnonce.setPrix(annonce.getPrix());


        annonceRepository.save(existingAnnonce);
    }




    public void deleteAnnonceById(int id) {
        annonceRepository.deleteById(id);
    }

    public void cancelAnnonce(int id) {
        Annonce annonce = annonceRepository.findById(id).orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        annonce.setCanceled(true);
        annonceRepository.save(annonce);
    }



    public Annonce saveAnnonce(Annonce annonce) {


        // Save the associated waypoints
        if (annonce.getWaypoints() != null) {
            for (Waypoint waypoint : annonce.getWaypoints()) {
                waypoint.setAnnonce(annonce);
            }
        }
        return annonceRepository.save(annonce);
    }


    public List<PickupPoint> getPickupPointsByAnnonce(int annonceId) {
        return pickupPointRepository.findByAnnonceId(annonceId);
    }

    public WaypointSuggestion proposeWaypoint(WaypointSuggestion waypointSuggestion) {
        return waypointSuggestionRepository.save(waypointSuggestion);
    }

    public List<WaypointSuggestion> getWaypointSuggestionsByAnnonce(int annonceId) {
        return waypointSuggestionRepository.findByAnnonceId(annonceId);
    }
    public List<Annonce> getAnnoncesByConducteur(Conducteur conducteur) {
        return annonceRepository.findByConducteur(conducteur);
    }


    public List<Annonce> searchRides(String lieuDepart, String lieuArrivee, String date_depart, Integer nbrPlaces, Float maxPrice) {
        return annonceRepository.searchRides(lieuDepart, lieuArrivee, date_depart, nbrPlaces, maxPrice);
    }
}