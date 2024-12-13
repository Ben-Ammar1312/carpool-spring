package gle.carpoolspring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.Conducteur;
import gle.carpoolspring.model.Waypoint;
import gle.carpoolspring.model.WaypointSuggestion;
import gle.carpoolspring.repository.AnnonceRepository;
import gle.carpoolspring.repository.ConducteurRepository;
import gle.carpoolspring.service.AnnonceService;
import gle.carpoolspring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

@Slf4j
@Controller
public class AnnonceController {

    @Autowired
    private UserService userService;

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private AnnonceRepository annonceRepository;
    @Autowired
    private ConducteurRepository conducteurRepository;

    @Value("${google.api.key}")
    private String googleApiKey;

    @GetMapping("/annonces/post-ride")
    public String postRidePage(Model model) {
        model.addAttribute("googleApiKey", googleApiKey);
        return "post-ride";
    }


    @PostMapping("/annonces/addAnnonce")
    public String addAnnonce(@ModelAttribute("annonce") Annonce annonce, Principal principal, HttpServletRequest request) {
        Conducteur conducteur = conducteurRepository.findByEmail(principal.getName());
        annonce.setConducteur(conducteur);

        Set<Waypoint> waypoints = new HashSet<>();
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

        annonceService.saveAnnonce(annonce);

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
            List<Annonce> myAnnonces = annonceService.getAnnoncesByConducteur(currentConducteur);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String annoncesJson = "";
            try {
                annoncesJson = mapper.writeValueAsString(myAnnonces);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            model.addAttribute("googleApiKey", googleApiKey);
            model.addAttribute("annonces", myAnnonces);
            model.addAttribute("annoncesJson", annoncesJson);
            return "liste_annonces";
        }

        @PostMapping("/allAnnonces/cancel/{id}")
        public String annulerAnnonce(@PathVariable int id) {
            System.out.println("ID de l'annonce à annuler : " + id);
            annonceService.cancelAnnonce(id);
            System.out.println("Annonce annulée avec succès");
            return "redirect:/allAnnonces";
        }



        @PostMapping("deleteAnnonce/{id}")
        public String deleteAnnonce(@PathVariable("id") int id) {
            annonceService.deleteAnnonceById(id);
            return "redirect:/allAnnonces";
        }


        @GetMapping("edit_annonce/{id}")
        public String showUpdateForm(@PathVariable("id") int id_annonce, Model model) {
            Annonce annonce = annonceService.getAnnonceById(id_annonce);
            model.addAttribute("annonce", annonce);
            model.addAttribute("googleApiKey", googleApiKey);
            return "edit_annonce";
        }

        @PostMapping("updateAnnonce/{id}")
        public String updateAnnonce(
                @PathVariable("id") int id,
                @ModelAttribute("annonce") Annonce annonce,
                RedirectAttributes redirectAttributes
        ) {
            annonce.setId_annonce(id);
            annonceService.updateAnnonce(annonce);
            return "redirect:/allAnnonces";
        }

    @GetMapping("/driver/suggestions")
    public String getDriverSuggestions(Model model, Principal principal) {
        String email = principal.getName();
        Conducteur driver = (Conducteur) userService.findByEmail(email);
        List<Annonce> driverAnnonces = annonceService.getAnnoncesByConducteur(driver);

        // Collect all suggestions from these annonces that are not approved or rejected
        Set<WaypointSuggestion> suggestions = new HashSet<>();
        for (Annonce annonce : driverAnnonces) {
            List<WaypointSuggestion> annonceSuggestions = annonceService.getWaypointSuggestionsByAnnonce(annonce.getId_annonce());
            for (WaypointSuggestion s : annonceSuggestions) {
                if (!s.isApprovedByDriver() && !s.isRejected()) {
                    suggestions.add(s);
                }
            }
        }

        model.addAttribute("suggestions", suggestions);
        return "manage-suggestions";
    }





    }












