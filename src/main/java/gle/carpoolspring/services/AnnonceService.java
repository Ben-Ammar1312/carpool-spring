package gle.carpoolspring.services;

import gle.carpoolspring.models.Annonce;
import gle.carpoolspring.repositories.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;




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
}
