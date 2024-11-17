package gle.carpoolspring.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_user;
    private String nom;
    private String prenom;
    private String num;
    private String email;
    private String password;
    private String adresse ;
    private String photo;
    private String cin;
    private Float note;


    @OneToMany(mappedBy = "sender")
    private List<gle.carpoolspring.models.Message> sentMessages;


    @OneToMany(mappedBy = "receiver")
    private List<gle.carpoolspring.models.Message> receivedMessages;


    @OneToMany(mappedBy = "user")
    private List<gle.carpoolspring.models.Reclamation> reclamations;
}
