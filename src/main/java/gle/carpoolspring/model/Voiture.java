package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Voiture {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_voiture;
    private String matricule;
    private String model;
    private String couleur;
    private String marque;

    // Many voitures can belong to one conducteur
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private gle.carpoolspring.model.Conducteur conducteur;


}
