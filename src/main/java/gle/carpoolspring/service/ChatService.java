package gle.carpoolspring.service;

import gle.carpoolspring.dto.ChatMessage;
import gle.carpoolspring.model.Chat;
import gle.carpoolspring.model.Message;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.ChatRepository;
import gle.carpoolspring.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AnnonceService annonceService;

    /**
     * Retrieve all chats for a user.
     */
    public List<Chat> getAllChatsForUser(User user) {
        List<Chat> chats = chatRepository.findByParticipantsContaining(user);
        logger.debug("Fetched {} chats for user ID {}", chats.size(), user.getIdUser());
        for (Chat chat : chats) {
            logger.debug("Chat ID: {}, Participants: {}", chat.getId(), chat.getParticipants().stream().map(User::getIdUser).collect(Collectors.toList()));
        }
        return chats;
    }


    /**
     * Retrieve messages for a chat.
     */

        public List<ChatMessage> getChatMessages(int chatId) {
            Chat chat = chatRepository.findChatWithParticipants(chatId);
            if (chat == null) {
                return Collections.emptyList();
            }

            return chat.getMessages().stream().map(msg -> {
                ChatMessage dto = new ChatMessage();
                dto.setSenderId(msg.getSender().getIdUser());
                dto.setReceiverId(msg.getReceiver().getIdUser());
                dto.setContent(msg.getContent());
                dto.setTimestamp(msg.getTimestamp());
                dto.setRideId(chat.getAnnonce().getIdAnnonce());
                dto.setSenderName(msg.getSender().getNom());
                return dto;
            }).collect(Collectors.toList());
        }


    /**
     * Retrieve or create a chat between a ride's driver and a passenger.
     */
    public Chat getOrCreateChat(int rideId, User passenger, User driver) {
        Optional<Chat> chatOpt = chatRepository.findByAnnonce_IdAnnonceAndParticipantsContaining(rideId, passenger);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            if (!chat.getParticipants().contains(driver)) {
                chat.addParticipant(driver);
                chatRepository.save(chat);
            }
            return chat;
        } else {
            Chat newChat = new Chat();
            newChat.addParticipant(passenger);
            newChat.addParticipant(driver);
            newChat.setAnnonce(annonceService.findById(rideId));
            return chatRepository.save(newChat);
        }
    }

    /**
     * Save a message to a chat.
     */
    public Message saveMessage(int chatId, Message message) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            message.setChat(chat);
            message.setTimestamp(LocalDateTime.now());
            return messageRepository.save(message);
        } else {
            throw new RuntimeException("Chat not found");
        }
    }
    @Transactional(readOnly = true)
    public Chat getChatById(int chatId) {
        return chatRepository.findChatWithParticipants(chatId);
    }


}