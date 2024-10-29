package gle.carpoolspring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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


}
