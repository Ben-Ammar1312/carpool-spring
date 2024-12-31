package gle.carpoolspring.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.User;
import gle.carpoolspring.service.AnnonceService;
import gle.carpoolspring.service.ReservationService;
import gle.carpoolspring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;
import java.util.Set;
@Slf4j
@Controller
public class SearchController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnonceService annonceService;

    @Value("${google.api.key}")
    private String googleApiKey;

    @GetMapping("/search")
    public String showSearchPage() {
        return "search"; // This corresponds to the HTML page for the search form
    }

    @GetMapping("/search/results")
    public String searchRides(
            @RequestParam String lieuDepart,
            @RequestParam(required = false) String lieuArrivee,
            @RequestParam(required = false) String dateDepart,
            @RequestParam(required = false) Integer nbrPlaces,
            @RequestParam(required = false) Float maxPrice,
            Model model,
            Principal principal
    ) {
        // If lieuArrivee is empty, set it to null
        if (lieuArrivee != null && lieuArrivee.isEmpty()) {
            lieuArrivee = null;
        }
        if (dateDepart != null && dateDepart.isEmpty()) {
            dateDepart = null;
        }
        String email = principal.getName();
        User currentUser = userService.findByEmail(email);
        List<Annonce> annonces = annonceService.searchRides(lieuDepart, lieuArrivee, dateDepart, nbrPlaces, maxPrice);

        Set<Integer> bookedAnnonceIds = reservationService.getBookedAnnonceIdsByUser(currentUser.getIdUser());
        Set<Integer> pendingAnnonceIds = reservationService.getPendingAnnonceIdsByUser(currentUser.getIdUser());
        // Add to model
        List<Annonce> userBookedRides = annonceService.findAllByIds(bookedAnnonceIds);
        for (Annonce bookedRide : userBookedRides) {
            if (!annonces.contains(bookedRide)) {
                annonces.add(bookedRide);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String annoncesJson = "";
        try {
            annoncesJson = mapper.writeValueAsString(annonces);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        model.addAttribute("bookedAnnonceIds", bookedAnnonceIds);
        model.addAttribute("pendingAnnonceIds", pendingAnnonceIds);
        model.addAttribute("annonces", annonces);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("annoncesJson", annoncesJson);
        model.addAttribute("googleApiKey", googleApiKey);
        return "search-results";
    }

}
