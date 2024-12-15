package gle.carpoolspring.service;

import gle.carpoolspring.model.Message;
import gle.carpoolspring.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getMessagesForUser(int userId) {
        return messageRepository.findAllByUserId(userId);
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public List<Message> getConversationMessages(int senderId, int receiverId) {
        return messageRepository.findMessagesBySenderAndReceiver(senderId, receiverId);
    }

    public List<Message> getUnreadMessages(int senderId, int receiverId) {
        return messageRepository.findUnreadMessagesBySenderAndReceiver(senderId, receiverId);
    }

    public int countUnreadMessagesForUser(int userId) {
        return messageRepository.countUnreadMessagesByReceiver(userId);
    }





}
