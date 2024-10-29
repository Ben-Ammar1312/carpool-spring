package gle.carpoolspring.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    @OneToMany(mappedBy = "sender")
    private List<gle.carpoolspring.models.Message> sentMessages;


    @OneToMany(mappedBy = "receiver")
    private List<gle.carpoolspring.models.Message> receivedMessages;


    @OneToMany(mappedBy = "user")
    private List<gle.carpoolspring.models.Reclamation> reclamations;
}
