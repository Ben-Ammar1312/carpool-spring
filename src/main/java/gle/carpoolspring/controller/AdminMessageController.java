package gle.carpoolspring.controller;


import gle.carpoolspring.model.ContactMessage;
import gle.carpoolspring.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/messages")
public class AdminMessageController {
    @Autowired
    private ContactMessageRepository contactMessageRepository;
    @GetMapping
    public String listMessages(Model model) {
        List<ContactMessage> messages = contactMessageRepository.findAll();
        model.addAttribute("messages", messages);
        return "/messages";
    }
    @GetMapping("/{id}")
    public String viewMessage(@PathVariable Long id, Model model) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable avec l'ID : " + id));
        model.addAttribute("message", message);
        return "/message-details";
    }
    @PostMapping("/{id}/mark-read")
    public String markAsRead(@PathVariable Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable avec l'ID : " + id));
        message.setStatus(ContactMessage.Status.READ);
        contactMessageRepository.save(message);
        return "/messages";
    }
    @PostMapping("/{id}/respond")
    public String respondToMessage(@PathVariable Long id,
                                   @RequestParam String response) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable avec l'ID : " + id));
        message.setResponse(response);
        message.setStatus(ContactMessage.Status.RESPONDED);
        contactMessageRepository.save(message);
        return "/messages";
    }
}