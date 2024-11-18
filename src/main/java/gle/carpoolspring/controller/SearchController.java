package gle.carpoolspring.controller;


import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.service.AnnonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private AnnonceService annonceService;

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
            Model model
    ) {
        // If lieuArrivee is empty, set it to null
        if (lieuArrivee != null && lieuArrivee.isEmpty()) {
            lieuArrivee = null;
        }
        if (dateDepart != null && dateDepart.isEmpty()) {
            dateDepart = null;
        }

        List<Annonce> annonces = annonceService.searchRides(lieuDepart, lieuArrivee, dateDepart, nbrPlaces, maxPrice);
        model.addAttribute("annonces", annonces);
        return "search-results";
    }

}
