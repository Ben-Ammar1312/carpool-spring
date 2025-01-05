package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.*;
import gle.carpoolspring.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAnnonce;
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
    @JsonManagedReference // Indicates this is the parent side
    private Conducteur conducteur;


    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL , orphanRemoval = true)
    @JsonManagedReference // Indicates this is the parent side
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "annonce",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference // Indicates this is the parent side
    private Set<WaypointSuggestion> waypointSuggestions;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference // Indicates this is the parent side
    private Set<Waypoint> waypoints;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Indicates this is the parent side
    private Set<PickupPoint> pickupPoints;

}
