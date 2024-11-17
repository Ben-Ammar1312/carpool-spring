package gle.carpoolspring.Controllers;

import gle.carpoolspring.models.Annonce;
import gle.carpoolspring.services.AnnonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AnnonceController {

    @Autowired
    private AnnonceService annulationAnnonceService;


    @RequestMapping("/allAnnonces")
    public String listAnnonces(Model model) {
        List<Annonce> listAnnonces = annulationAnnonceService.getAllAnnonces();
        model.addAttribute("listAnnonces", listAnnonces);
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


    @GetMapping("editAnnonce/{id}")
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
