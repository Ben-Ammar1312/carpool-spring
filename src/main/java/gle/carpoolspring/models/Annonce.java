package gle.carpoolspring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_annonce;
    String date_depart;
    int nbrPlaces;
    String lieuArrivee;
    String lieuDepart;
    String heureDepart;
    Float prix;


}
