package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_message;
    String date_message;
    String content_message;
    // Many messages can be sent by one user (sender)
    @ManyToOne
    @JoinColumn(name = "id_sender", nullable = false)
    private User sender;

    // Many messages can be received by one user (receiver)
    @ManyToOne
    @JoinColumn(name = "id_receiver", nullable = false)
    private User receiver;
}
