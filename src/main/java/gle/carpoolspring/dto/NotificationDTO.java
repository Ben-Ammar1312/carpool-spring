package gle.carpoolspring.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long notificationId;
    private String message;
    private boolean read;
    private LocalDateTime timestamp;

    // Constructors
    public NotificationDTO() {}

    public NotificationDTO(Long notificationId, String message, boolean read, LocalDateTime timestamp) {
        this.notificationId = notificationId;
        this.message = message;
        this.read = read;
        this.timestamp = timestamp;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}