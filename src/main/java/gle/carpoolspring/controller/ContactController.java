package gle.carpoolspring.controller;

import gle.carpoolspring.model.ContactForm;
import gle.carpoolspring.model.ContactMessage;
import gle.carpoolspring.repository.ContactMessageRepository;
import gle.carpoolspring.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @GetMapping
    public String showContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm()); // Ajout de l'attribut
        return "contact"; // Nom du fichier HTML (contact.html)
    }

    @PostMapping
    public String submitContactForm(@ModelAttribute("contactForm") ContactForm contactForm,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Enregistrer dans la base de données
            ContactMessage savedMessage = new ContactMessage();
            savedMessage.setName(contactForm.getName());
            savedMessage.setEmail(contactForm.getEmail());
            savedMessage.setSubject(contactForm.getSubject());
            savedMessage.setMessage(contactForm.getMessage());
            savedMessage.setSentAt(LocalDateTime.now());
            contactMessageRepository.save(savedMessage);

            // Envoi de l'email
            emailService.sendEmail(
                    "ouledbouheddanada28@gmail.com",
                    contactForm.getSubject(),
                    String.format("Message from %s (%s):\n\n%s",
                            contactForm.getName(),
                            contactForm.getEmail(),
                            contactForm.getMessage())
            );

            // Ajouter un message de succès
            redirectAttributes.addFlashAttribute("successMessage", "Your message has been sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            // Ajouter un message d'erreur
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while sending your message.");
        }

        return "redirect:/contact";
    }
}