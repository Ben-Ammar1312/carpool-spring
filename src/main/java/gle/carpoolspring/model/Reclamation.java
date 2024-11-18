package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_reclamation;
    private String sujet;
    private String titre;
    private String reponse;


    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
