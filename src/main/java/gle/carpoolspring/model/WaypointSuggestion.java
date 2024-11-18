package gle.carpoolspring.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
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
    private Annonce annonce;

    @ManyToMany
    @JoinTable(
            name="waypoint_approval",
            joinColumns = @JoinColumn(name="waypoint_id"),
            inverseJoinColumns = @JoinColumn(name="pasenger_id")
    )
    List<Passager> approvedByPassengers;
    private boolean isRejected = false;
}
