package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId; // The recipient's user ID
    private String message;
    @Column(name = "is_read")
    private boolean isRead;
    private LocalDateTime timestamp;

    // Constructors, getters, and setters
}