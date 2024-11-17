package gle.carpoolspring.service;

import gle.carpoolspring.models.Annonce;

import gle.carpoolspring.repository.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    public List<Annonce> searchRides(String lieuDepart, String lieuArrivee, String date_depart, Integer nbrPlaces, Float maxPrice) {
        return annonceRepository.searchRides(lieuDepart, lieuArrivee, date_depart, nbrPlaces, maxPrice);
    }
}
