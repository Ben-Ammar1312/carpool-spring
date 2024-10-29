package gle.carpoolspring.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_user;
    String nom;
    String prenom;
    String num;
    String email;
    String password;
    String adresse ;
    String photo;
    String cin;
    Float note;

    // One user can send many messages
    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;

    // One user can receive many messages
    @OneToMany(mappedBy = "receiver")
    private List<Message> receivedMessages;
}
