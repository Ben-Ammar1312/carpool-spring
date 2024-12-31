package gle.carpoolspring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gle.carpoolspring.dto.AnnonceForm;
import gle.carpoolspring.dto.WaypointForm;
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
        model.addAttribute("annonceForm", new AnnonceForm());
        model.addAttribute("googleApiKey", googleApiKey);
        return "post-ride";
    }


    @PostMapping("/annonces/addAnnonce")
    public String addAnnonce(
            @ModelAttribute("annonceForm") AnnonceForm annonceForm,
            Principal principal
    ) {
        // 1) Find the driver
        Conducteur conducteur = conducteurRepository.findByEmail(principal.getName());

        // 2) Create a real Annonce
        Annonce annonce = new Annonce();
        annonce.setConducteur(conducteur);

        // 3) Copy fields from the form
        annonce.setLieuDepart(annonceForm.getLieuDepart());
        annonce.setLieuArrivee(annonceForm.getLieuArrivee());
        annonce.setDepartLat(annonceForm.getDepartLat());
        annonce.setDepartLng(annonceForm.getDepartLng());
        annonce.setArriveLat(annonceForm.getArriveLat());
        annonce.setArriveLng(annonceForm.getArriveLng());
        annonce.setDateDepart(annonceForm.getDateDepart());
        annonce.setHeureDepart(annonceForm.getHeureDepart());
        annonce.setNbrPlaces(annonceForm.getNbrPlaces());
        annonce.setPrix(annonceForm.getPrix());

        // 4) Build the Set<Waypoint> from the form's List<WaypointForm>
        Set<Waypoint> waypoints = new HashSet<>();
        for (WaypointForm wf : annonceForm.getWaypoints()) {
            if (wf.getAddress() != null && !wf.getAddress().trim().isEmpty()) {
                Waypoint waypoint = new Waypoint();
                waypoint.setAddress(wf.getAddress());
                waypoint.setLatitude(wf.getLatitude());
                waypoint.setLongitude(wf.getLongitude());
                waypoint.setAnnonce(annonce);
                waypoints.add(waypoint);
            }
        }
        annonce.setWaypoints(waypoints.isEmpty() ? null : waypoints);

        // 5) Save
        annonceService.saveAnnonce(annonce);
        System.out.println("Saving Annonce: " + annonce);

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
        public String showUpdateForm(@PathVariable("id") int idAnnonce, Model model) {
            Annonce annonce = annonceService.getAnnonceById(idAnnonce);
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
            annonce.setIdAnnonce(id);
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
            List<WaypointSuggestion> annonceSuggestions = annonceService.getWaypointSuggestionsByAnnonce(annonce.getIdAnnonce());
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












