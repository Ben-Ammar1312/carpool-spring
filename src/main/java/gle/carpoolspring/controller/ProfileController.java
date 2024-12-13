package gle.carpoolspring.controller;

import gle.carpoolspring.model.User;
import gle.carpoolspring.service.UserService;
import gle.carpoolspring.service.StorageService;  // Make sure StorageService is injected
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
@Slf4j
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;  // Inject StorageService for file uploads

    @GetMapping
    public String showProfile(ModelMap model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "profile";
    }


  // Make sure this service checks for existing email

    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute("user") User user,
            @RequestParam(value = "photoName", required = false) String photoName, // Accept photo name if it is provided
            RedirectAttributes redirectAttributes) {

        // Retrieve the current user from the database using email
        User currentUser = userService.findByEmail(user.getEmail());

        // Update only the fields that should be updated
        if (currentUser != null) {
            // Update the fields that the user is allowed to change
            if (user.getNom() != null && !user.getNom().isEmpty()) {
                currentUser.setNom(user.getNom());
            }
            if (user.getPrenom() != null && !user.getPrenom().isEmpty()) {
                currentUser.setPrenom(user.getPrenom());
            }
            if (user.getTelephone() != null && !user.getTelephone().isEmpty()) {
                currentUser.setTelephone(user.getTelephone());
            }
            if (user.getAdresse() != null && !user.getAdresse().isEmpty()) {
                currentUser.setAdresse(user.getAdresse());
            }
            if (photoName != null && !photoName.isEmpty()) {
                currentUser.setPhoto(photoName);  // Only update photo if provided
            }

            // Optionally, update password if it's provided (ensure it is hashed)
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                currentUser.setPassword(encoder.encode(user.getPassword()));
            }

            // Save the updated user
            userService.update(currentUser);

            // Add success message and redirect
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        }

        return "redirect:/profile";  // Redirect back to the profile page
    }





    @PostMapping("/delete")
    public String deleteAccount(Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        userService.deleteAccount(email);
        redirectAttributes.addFlashAttribute("success", "Account deleted successfully.");
        return "redirect:/logout";
    }
}
