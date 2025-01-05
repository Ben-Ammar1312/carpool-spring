package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gle.carpoolspring.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id_reclamation")
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
