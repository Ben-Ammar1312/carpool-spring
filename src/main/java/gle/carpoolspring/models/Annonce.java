package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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


    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Conducteur conducteur;


    @OneToMany(mappedBy = "annonces")
    private List<Reservation> reservations;



}
