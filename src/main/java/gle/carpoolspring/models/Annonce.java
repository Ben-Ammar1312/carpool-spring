package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Setter
@Getter
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_annonce;
    private LocalDate dateDepart;
    private int nbrPlaces;
    private String lieuArrivee;
    private String lieuDepart;
    private String heureDepart;
    private Float prix;


    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Conducteur conducteur;


    @OneToMany(mappedBy = "annonces")
    private List<Reservation> reservations;

    boolean isCanceled = false;



}
