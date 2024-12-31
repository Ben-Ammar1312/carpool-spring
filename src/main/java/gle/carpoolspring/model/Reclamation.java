package gle.carpoolspring.model;

import gle.carpoolspring.enums.Status;
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

    @Enumerated(EnumType.STRING)
    private Status status;


    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
