package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Voiture {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_voiture;
    String matricule;
    String model;
    String couleur;
    String marque;

    // Many voitures can belong to one conducteur
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private gle.carpoolspring.models.Conducteur conducteur;


}
