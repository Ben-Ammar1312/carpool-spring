package gle.carpoolspring.controller;

import gle.carpoolspring.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, NotificationDTO message) {
        System.out.println("Sending notification to userId=" + userId + " with message: " + message.getMessage());
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, message);
    }
}