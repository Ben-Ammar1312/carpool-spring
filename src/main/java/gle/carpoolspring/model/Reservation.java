package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gle.carpoolspring.enums.Etat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id_reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_reservation;
    private String date_reservation;
    private int nbrPlaces;

    @Enumerated(EnumType.STRING)
    private Etat etat;


    @ManyToOne
    @JoinColumn(name = "id_passager", nullable = false)
    private gle.carpoolspring.model.Passager passager;


    @ManyToOne
    @JoinColumn(name = "id_annonce", nullable = false)
    @JsonBackReference
    private gle.carpoolspring.model.Annonce annonce;

    @OneToOne
    @JoinColumn(name = "id_paiement")
    private gle.carpoolspring.model.Paiement paiement;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaypointSuggestion> waypointSuggestions = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waypoint> waypoints = new ArrayList<>();

}
