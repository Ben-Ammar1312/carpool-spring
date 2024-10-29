package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_reclamation;
    String sujet;
    String titre;
    String reponse;


    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
