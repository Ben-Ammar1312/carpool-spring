package gle.carpoolspring.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class WaypointSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double latitude;
    private Double longitude;
    private String address;
    private boolean approvedByDriver=false;

    @ManyToOne
    @JoinColumn(name="id_annonce")
    @JsonBackReference
    private Annonce annonce;

    @ManyToMany
    @JoinTable(
            name="waypoint_approval",
            joinColumns = @JoinColumn(name="waypoint_id"),
            inverseJoinColumns = @JoinColumn(name="pasenger_id")
    )
    List<Passager> approvedByPassengers;
    private boolean isRejected = false;

    // Add a foreign key to Reservation if needed
    @ManyToOne
    @JoinColumn(name = "id_reservation")
    @JsonIgnore
    private Reservation reservation;
}
