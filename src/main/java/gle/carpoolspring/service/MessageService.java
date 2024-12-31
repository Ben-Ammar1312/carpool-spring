package gle.carpoolspring.service;

import gle.carpoolspring.model.Chat;
import gle.carpoolspring.model.Message;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.ChatRepository;
import gle.carpoolspring.repository.MessageRepository;
import gle.carpoolspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;


    public List<Message> getMessagesForChat(int chatId) {
        // Ensure messages are sorted ascending by timestamp
        return messageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }

    public void sendMessage(int chatId, String messageContent, String senderUsername) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        User sender = userRepository.findByEmail(senderUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = new Message();
        message.setContent(messageContent);
        message.setTimestamp(LocalDateTime.now());
        message.setChat(chat);
        message.setSender(sender);

        messageRepository.save(message);
    }
    /**
     * Save a message.
     */
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    /**
     * Fetch messages by ride ID.
     */
    public List<Message> getMessagesByRideId(int rideId) {
        return messageRepository.findByChat_IdOrderByTimestampAsc(rideId);
    }

    // Additional methods as needed
}