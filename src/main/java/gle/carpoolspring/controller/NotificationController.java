package gle.carpoolspring.controller;

import gle.carpoolspring.dto.NotificationRequest;
import gle.carpoolspring.model.Notification;
import gle.carpoolspring.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable int userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }



    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest request) {
        Notification notification = notificationService.createNotification(request.getUserId(), request.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable int notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}