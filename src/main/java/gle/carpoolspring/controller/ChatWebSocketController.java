package gle.carpoolspring.controller;
import gle.carpoolspring.dto.ChatMessage;
import gle.carpoolspring.model.Chat;
import gle.carpoolspring.model.Message;
import gle.carpoolspring.model.User;
import gle.carpoolspring.service.ChatService;
import gle.carpoolspring.service.MessageService;
import gle.carpoolspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat/{chatId}")
    public void sendMessage(@DestinationVariable int chatId, @Payload ChatMessage chatMessage, Principal principal) {
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

        // Determine the receiver based on chat participants and chatMessage content
        int receiverId = chatMessage.getReceiverId();
        User receiver = userService.findById(receiverId);
        if (receiver == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found");
        }

        // Ensure the receiver is a participant in the chat
        if (!chat.getParticipants().contains(receiver)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver is not a participant in the chat");
        }

        Message message = new Message();
        message.setSender(currentUser);
        message.setReceiver(receiver);
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setChat(chat);
        message.setAnnonce(chat.getAnnonce());
        message.setRead(false);

        // Save the message to the database
        messageService.saveMessage(message);

        // Prepare the ChatMessage DTO to broadcast
        ChatMessage broadcastMessage = new ChatMessage();
        broadcastMessage.setSenderId(currentUser.getIdUser());
        broadcastMessage.setReceiverId(receiver.getIdUser());
        broadcastMessage.setContent(message.getContent());
        broadcastMessage.setSenderName(message.getSender().getNom());
        broadcastMessage.setTimestamp(message.getTimestamp());
        broadcastMessage.setRideId(chat.getAnnonce().getIdAnnonce());

        // Broadcast to all participants of the chat
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, broadcastMessage);
    }
}