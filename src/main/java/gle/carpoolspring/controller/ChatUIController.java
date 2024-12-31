package gle.carpoolspring.controller;

import gle.carpoolspring.dto.ChatMessage;
import gle.carpoolspring.model.*;
import gle.carpoolspring.service.AnnonceService;
import gle.carpoolspring.service.ChatService;
import gle.carpoolspring.service.MessageService;
import gle.carpoolspring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatUIController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ChatUIController.class);


    /**
     * General Chat Page - Lists all chats for the current user.
     */
    @GetMapping("/chat")
    public String generalChatPage(Model model, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Authenticated");
        }
        List<Chat> chats = chatService.getAllChatsForUser(currentUser);
        // Log chat details
        for (Chat chat : chats) {
            logger.info("Chat ID: {}, Participants: {}", chat.getId(), chat.getParticipants().stream().map(User::getIdUser).collect(Collectors.toList()));
        }
        if (currentUser instanceof Conducteur) {
            Conducteur conducteur = (Conducteur) currentUser;
            model.addAttribute("currentUser", conducteur);
        } else {
            Passager passenger = (Passager) currentUser;
            model.addAttribute("currentUser", passenger);
        }
        model.addAttribute("chats", chats);
        // Optionally, add unread messages count
        return "general-chat"; // Ensure you have 'general-chat.html'
    }

    /**
     * Ride-Specific Chat Page - Chat between a passenger and the driver.
     */
    @GetMapping("/chat/{rideId}/{participantId}")
    public String rideChatPage(@PathVariable("rideId") int rideId,
                               @PathVariable("participantId") int participantId,
                               Model model, Principal principal) {
        Annonce annonce = annonceService.findById(rideId);
        if (annonce == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");
        }

        User participant = userService.findById(participantId);
        if (participant == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found");
        }

        User driver = annonce.getConducteur();
        User currentUser = userService.findByEmail(principal.getName());


        Chat chat = chatService.getOrCreateChat(rideId, participant, driver);
        List<ChatMessage> messages = chatService.getChatMessages(chat.getId());

        // Determine the passenger based on the current user's role
        User passenger;
        if (currentUser.getIdUser() == driver.getIdUser()) {
            // Current user is the driver; passenger is the other participant
            passenger = participant;
        } else {
            // Current user is the passenger; passenger is themselves
            passenger = currentUser;
        }

        model.addAttribute("ride", annonce);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("passenger", passenger); // Ensure 'passenger' is always set
        model.addAttribute("chatId", chat.getId());
        model.addAttribute("messages", messages);

        return "chat"; // Ensure you have 'chat.html'
    }
}