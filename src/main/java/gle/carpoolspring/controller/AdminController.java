package gle.carpoolspring.controller;

import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.AnnonceRepository;
import gle.carpoolspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin_dashboard";
    }

    // Display Manage Users page
    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "manage_users";
    }

    // Handle User Deletion
    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam("id") Integer userId) {
        userRepository.deleteById(userId);
        return "redirect:/admin/manage-users";
    }

    // Handle User Updates
    @PostMapping("/update-user")
    public String updateUser(
            @RequestParam("id") Integer userId,
            @RequestParam("email") String email,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("telephone") String telephone
    ) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setEmail(email);
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setTelephone(telephone);
        userRepository.save(user);
        return "redirect:/admin/manage-users";
    }

    // Endpoint to display all announcements
    @GetMapping("/manage-announcements")
    public String manageAnnouncements(Model model) {
        List<Annonce> annonces = annonceRepository.findAll();
        model.addAttribute("annonces", annonces);
        return "manage-announcements";
    }

    // Endpoint to handle deletion of an announcement
    @PostMapping("/delete-announcement")
    public String deleteAnnouncement(@RequestParam("id_annonce") int idAnnonce) {
        Optional<Annonce> annonce = annonceRepository.findById(idAnnonce);
        annonce.ifPresent(value -> annonceRepository.delete(value));
        return "redirect:/admin/manage-announcements";
    }





}
