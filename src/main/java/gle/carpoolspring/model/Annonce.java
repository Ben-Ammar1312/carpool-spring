package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_annonce;
    private LocalDate dateDepart;
    private Float prix;
    private String heureDepart;
    private String lieuArrivee;
    private String lieuDepart;
    private int nbrPlaces;
    private boolean isCanceled = false;
    private Double departLat;
    private Double departLng;
    private Double arriveLat;
    private Double arriveLng;



    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Conducteur conducteur;


    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "annonce",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<WaypointSuggestion> waypointSuggestions;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnoreProperties("annonce")
    private List<Waypoint> waypoints;



}
