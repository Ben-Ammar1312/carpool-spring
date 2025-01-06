package gle.carpoolspring.controller;

import gle.carpoolspring.enums.Status;
import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.Paiement;
import gle.carpoolspring.model.Reclamation;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.AnnonceRepository;
import gle.carpoolspring.repository.PaiementRepository;
import gle.carpoolspring.repository.ReclamationRepository;
import gle.carpoolspring.repository.UserRepository;
import gle.carpoolspring.service.ReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Fetch data for reclamation statistics
        long openReclamations = reclamationRepository.countByStatus(Status.Open);
        long cancelledReclamations = reclamationRepository.countByStatus(Status.Cancelled);
        long resolvedReclamations = reclamationRepository.countByStatus(Status.Full);

        // Fetch data for user statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabled(true);

        // Fetch data for announcements
        long activeAnnouncements = annonceRepository.countByStatus(Status.Open);

        // Add data to the model
        model.addAttribute("openReclamations", openReclamations);
        model.addAttribute("cancelledReclamations", cancelledReclamations);
        model.addAttribute("resolvedReclamations", resolvedReclamations);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("activeAnnouncements", activeAnnouncements);

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


    @Autowired
    private ReclamationRepository reclamationRepository;

    // Endpoint to display all complaints (open complaints will appear first)
    @GetMapping("/manage-complaints")
    public String manageComplaints(Model model) {
        List<Reclamation> reclamations = reclamationRepository.findAllByOrderByStatusDesc();
        model.addAttribute("reclamations", reclamations);
        return "manage-complaints";
    }

    // Endpoint to delete a complaint
    @PostMapping("/delete-complaint")
    public String deleteComplaint(@RequestParam("id_reclamation") int idReclamation) {
        Optional<Reclamation> reclamation = reclamationRepository.findById(idReclamation);
        reclamation.ifPresent(value -> reclamationRepository.delete(value));
        return "redirect:/admin/manage-complaints";
    }

    // Endpoint to view a specific complaint
    @GetMapping("/view-complaint/{id}")
    public String viewComplaint(@PathVariable("id") int idReclamation, Model model) {
        Optional<Reclamation> reclamation = reclamationRepository.findById(idReclamation);
        if (reclamation.isPresent()) {
            model.addAttribute("reclamation", reclamation.get());
            return "view-complaint";  // View page where admin can see details of the complaint
        }
        return "redirect:/admin/manage-complaints";  // In case the complaint is not found
    }

    @Autowired
    private ReclamationService reclamationService;

    @PostMapping("/update-complaint-status") // POST request to update complaint status
    public String updateComplaintStatus(@RequestParam("id_reclamation") int idReclamation,
                                        @RequestParam("status") String status,
                                        RedirectAttributes redirectAttributes) {
        // Fetch the complaint by id
        Reclamation reclamation = reclamationService.findById(idReclamation);

        if (reclamation != null) {
            // Update the status
            reclamation.setStatus(Status.valueOf(status));
            reclamationService.save(reclamation);

            // Add a success message to the redirect
            redirectAttributes.addFlashAttribute("message", "Complaint status updated successfully.");
        } else {
            // Handle the case when the complaint is not found
            redirectAttributes.addFlashAttribute("error", "Complaint not found.");
        }

        // Redirect back to the manage complaints page
        return "redirect:/admin/manage-complaints";
    }

    @PostMapping("/update-complaint-response")
    public String updateComplaintResponse(@RequestParam("id_reclamation") int idReclamation,
                                          @RequestParam("response") String response,
                                          RedirectAttributes redirectAttributes) {
        Reclamation reclamation = reclamationService.findById(idReclamation);

        if (reclamation != null) {
            reclamation.setReponse(response);
            reclamationService.save(reclamation);

            redirectAttributes.addFlashAttribute("message", "Complaint response updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Complaint not found.");
        }

        return "redirect:/admin/manage-complaints";
    }


    @GetMapping("/manage-payments")
    public String managePayments(Model model) {
        List<Paiement> paiements = paiementRepository.findAll();
        model.addAttribute("paiements", paiements);
        return "manage-payments";
    }

    @GetMapping("/manage-refund/{paymentIntentId}")
    public String manageRefund(@PathVariable("paymentIntentId") String paymentIntentId, Model model, RedirectAttributes redirectAttributes) {
        // Fetch the Paiement entity by paymentIntentId
        Paiement paiement = paiementRepository.findByPaymentIntentId(paymentIntentId);
        if (paiement == null) {
            // Add an error message and redirect back to manage payments
            redirectAttributes.addFlashAttribute("error", "Payment not found.");
            return "redirect:/admin/manage-payments";
        }

        // Add the Paiement to the model
        model.addAttribute("paiement", paiement);
        return "manage-refund"; // Thymeleaf template name
    }




}
