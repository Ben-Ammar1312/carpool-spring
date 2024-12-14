package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.*;
import gle.carpoolspring.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id_annonce")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_annonce;
    private LocalDate dateDepart;
    @DecimalMin(value = "0.0")
    private Float prix;
    private String heureDepart;
    private String lieuArrivee;
    private String lieuDepart;
    @Min(1)
    private int nbrPlaces;
    private boolean isCanceled = false;
    private Double departLat;
    private Double departLng;
    private Double arriveLat;
    private Double arriveLng;
    @Enumerated(EnumType.STRING)
    private Status status;



    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    @JsonManagedReference
    private Conducteur conducteur;


    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL , orphanRemoval = true)
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "annonce",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<WaypointSuggestion> waypointSuggestions;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Waypoint> waypoints;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PickupPoint> pickupPoints;

}
