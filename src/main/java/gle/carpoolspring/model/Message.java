package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_message;

    private String date_message;
    public String content_message;

    @ManyToOne
    @JoinColumn(name = "id_sender", nullable = false)
    private User sender; // Sender user

    @ManyToOne
    @JoinColumn(name = "id_receiver", nullable = false)
    private User receiver; // Receiver user
}


