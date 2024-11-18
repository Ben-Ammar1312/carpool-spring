package gle.carpoolspring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.Conducteur;
import gle.carpoolspring.model.Waypoint;
import gle.carpoolspring.repository.AnnonceRepository;
import gle.carpoolspring.repository.ConducteurRepository;
import gle.carpoolspring.service.AnnonceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AnnonceController {
    @Autowired
    private AnnonceService annulationAnnonceService;

    @Autowired
    private AnnonceRepository annonceRepository;
    @Autowired
    private ConducteurRepository conducteurRepository;

    @GetMapping("/annonces/post-ride")
    public String postRidePage() {
        return "post-ride"; // Correspond au nom de votre fichier HTML (post-ride.html)
    }


    @PostMapping("/annonces/addAnnonce")
    public String addAnnonce(@ModelAttribute("annonce") Annonce annonce, Principal principal, HttpServletRequest request) {
        Conducteur conducteur = conducteurRepository.findByEmail(principal.getName());
        annonce.setConducteur(conducteur);

        List<Waypoint> waypoints = new ArrayList<>();
        int index = 0;
        String address;

        while ((address = request.getParameter("waypoints[" + index + "].address")) != null) {
            address = address.trim();
            if (!address.isEmpty()) {
                Waypoint waypoint = new Waypoint();
                waypoint.setAddress(address);
                String latParam = request.getParameter("waypoints[" + index + "].latitude");
                String lngParam = request.getParameter("waypoints[" + index + "].longitude");
                if (latParam != null && lngParam != null && !latParam.isEmpty() && !lngParam.isEmpty()) {
                    try {
                        waypoint.setLatitude(Double.parseDouble(latParam));
                        waypoint.setLongitude(Double.parseDouble(lngParam));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid latitude or longitude for waypoint index " + index);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Missing latitude or longitude for waypoint index " + index);
                }
                waypoints.add(waypoint);
            }
            index++;
        }

        String departLat = request.getParameter("lieuDepartLat");
        String departLng = request.getParameter("lieuDepartLng");
        String arriveeLat = request.getParameter("lieuArriveeLat");
        String arriveeLng = request.getParameter("lieuArriveeLng");

        if (departLat != null && !departLat.isEmpty() && departLng != null && !departLng.isEmpty()) {
            try {
                annonce.setDepartLat(Double.parseDouble(departLat));
                annonce.setDepartLng(Double.parseDouble(departLng));
            } catch (NumberFormatException e) {
                System.err.println("Invalid departure latitude or longitude.");
                e.printStackTrace();
            }
        } else {
            System.err.println("Missing departure latitude or longitude.");
        }

        if (arriveeLat != null && !arriveeLat.isEmpty() && arriveeLng != null && !arriveeLng.isEmpty()) {
            try {
                annonce.setArriveLat(Double.parseDouble(arriveeLat));
                annonce.setArriveLng(Double.parseDouble(arriveeLng));
            } catch (NumberFormatException e) {
                System.err.println("Invalid arrival latitude or longitude.");
                e.printStackTrace();
            }
        } else {
            System.err.println("Missing arrival latitude or longitude.");
        }

        annonce.setWaypoints(waypoints.isEmpty() ? null : waypoints);
        annonce.setLieuDepart(request.getParameter("lieuDepart"));
        annonce.setLieuArrivee(request.getParameter("lieuArrivee"));

        // Log the saved annonce for debugging
        System.out.println("Saving Annonce: " + annonce);

        annulationAnnonceService.saveAnnonce(annonce);
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


   /* @GetMapping("/ride-history/{conducteurId}")
    public String getRideHistory(@PathVariable int conducteurId, ModelMap model) {
        List<Annonce> rideHistory = annonceRepository.findByConducteur_IdUser(conducteurId);
        model.addAttribute("rideHistory", rideHistory);
        return "ride-history";
    }*/







        @RequestMapping("/allAnnonces")
        public String listAnnonces(Model model , Principal principal) {
            Conducteur currentConducteur = conducteurRepository.findByEmail(principal.getName());
            List<Annonce> myAnnonces = annulationAnnonceService.getAnnoncesByConducteur(currentConducteur);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String annoncesJson = "";
            try {
                annoncesJson = mapper.writeValueAsString(myAnnonces);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            model.addAttribute("annonces", myAnnonces);
            model.addAttribute("annoncesJson", annoncesJson);
            return "liste_annonces";
        }

        @PostMapping("/allAnnonces/cancel/{id}")
        public String annulerAnnonce(@PathVariable int id) {
            System.out.println("ID de l'annonce à annuler : " + id);
            annulationAnnonceService.cancelAnnonce(id);
            System.out.println("Annonce annulée avec succès");
            return "redirect:/allAnnonces";
        }



        @PostMapping("deleteAnnonce/{id}")
        public String deleteAnnonce(@PathVariable("id") int id) {
            annulationAnnonceService.deleteAnnonceById(id);
            return "redirect:/allAnnonces";
        }


        @GetMapping("edit_annonce/{id}")
        public String showUpdateForm(@PathVariable("id") int id_annonce, Model model) {
            Annonce annonce = annulationAnnonceService.getAnnonceById(id_annonce);
            model.addAttribute("annonce", annonce);
            return "edit_annonce";
        }

        @PostMapping("updateAnnonce/{id}")
        public String updateAnnonce(
                @PathVariable("id") int id,
                @ModelAttribute("annonce") Annonce annonce,
                RedirectAttributes redirectAttributes
        ) {
            annonce.setId_annonce(id);
            annulationAnnonceService.updateAnnonce(annonce);
            return "redirect:/allAnnonces";
        }





    }












