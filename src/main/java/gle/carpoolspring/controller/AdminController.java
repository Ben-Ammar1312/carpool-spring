package gle.carpoolspring.controller;

import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

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



}
