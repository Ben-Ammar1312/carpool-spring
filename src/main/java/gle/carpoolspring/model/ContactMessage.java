package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ContactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String message;
    private String subject;
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNREAD; // UNREAD, READ, RESPONDED

    @Column(length = 1000)
    private String response; // Réponse de l'administrateur

    public enum Status {
        UNREAD, READ, RESPONDED
    }
}

