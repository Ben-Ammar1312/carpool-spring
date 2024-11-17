package gle.carpoolspring.controller;

import gle.carpoolspring.models.Annonce;
import gle.carpoolspring.repositories.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/annonces")
public class AnnonceController {

    @Autowired
    private AnnonceRepository annonceRepository;

    @GetMapping("/post-ride")
    public String postRidePage() {
        return "post-ride"; // Correspond au nom de votre fichier HTML (post-ride.html)
    }


    @PostMapping("/addAnnonce")
    public String addAnnonce(@ModelAttribute("annonce") Annonce annonce) {
        annonceRepository.save(annonce); // Sauvegarde l'annonce dans la base de données
        return "redirect:/annonces/post-ride?success";

    }


    @GetMapping
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }


    @GetMapping("/{id}")
    public Annonce getAnnonceById(@PathVariable int id) {
        return annonceRepository.findById(id).orElse(null);
    }


    /*@DeleteMapping("/{id}")
    public String deleteAnnonce(@PathVariable int id) {
        annonceRepository.deleteById(id);
        return "Annonce supprimée avec succès!";
    }*/


    @GetMapping("/ride-history/{conducteurId}")
    public String getRideHistory(@PathVariable int conducteurId, ModelMap model) {
        List<Annonce> rideHistory = annonceRepository.findByConducteur_id_user(conducteurId);
        model.addAttribute("rideHistory", rideHistory);
        return "ride-history";
    }

}



