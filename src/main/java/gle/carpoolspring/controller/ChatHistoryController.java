package gle.carpoolspring.controller;
import gle.carpoolspring.dto.ChatMessage;
import gle.carpoolspring.model.Chat;
import gle.carpoolspring.model.Message;
import gle.carpoolspring.model.User;
import gle.carpoolspring.service.AnnonceService;
import gle.carpoolspring.service.ChatService;
import gle.carpoolspring.service.MessageService;
import gle.carpoolspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private ChatService chatService;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@RequestParam int chatId, Principal principal) {
        // Fetch the current authenticated user
        String username = principal.getName();
        User currentUser = userService.findByEmail(username);
        int userId = currentUser.getIdUser();

        // Fetch the chat details
        Chat chat = chatService.getChatById(chatId);
        if (chat == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found");
        }

        // Verify that the current user is a participant in the chat
        boolean isParticipant = chat.getParticipants().stream()
                .anyMatch(participant -> participant.getIdUser() == userId);

        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Fetch messages related to the chat, sorted by timestamp ascending
        List<Message> messages = messageService.getMessagesForChat(chatId);

        // Convert Message entities to ChatMessage DTOs
        List<ChatMessage> chatMessages = messages.stream().map(msg -> {
            ChatMessage cm = new ChatMessage();
            cm.setSenderId(msg.getSender().getIdUser());
            cm.setReceiverId(msg.getReceiver().getIdUser());
            cm.setContent(msg.getContent());
            cm.setTimestamp(msg.getTimestamp());
            cm.setRideId(msg.getAnnonce().getIdAnnonce());
            return cm;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(chatMessages);
    }
}