package gle.carpoolspring.service;

import gle.carpoolspring.controller.NotificationWebSocketController;
import gle.carpoolspring.dto.NotificationDTO;
import gle.carpoolspring.model.Notification;
import gle.carpoolspring.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationWebSocketController notificationWebSocketController;

    public List<Notification> getUserNotifications(int userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<Notification> getUnreadNotifications(int userId) {
        return notificationRepository.findUnreadByUserIdOrderByTimestampDesc(userId);
    }

    public Notification createNotification(int userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());
        Notification savedNotification =  notificationRepository.save(notification);
        NotificationDTO dto = new NotificationDTO(
                (long) savedNotification.getId(),
                savedNotification.getMessage(),
                savedNotification.isRead(),
                savedNotification.getTimestamp()
        );
        notificationWebSocketController.sendNotification((long) userId, dto);
        return savedNotification;
    }

    public void markAsRead(int notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}