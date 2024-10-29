package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_message;
    String date_message;
    String content_message;



    @ManyToOne
    @JoinColumn(name = "id_sender", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "id_receiver",nullable = false)
    private User receiver;

}
