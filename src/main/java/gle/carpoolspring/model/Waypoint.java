package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Waypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double latitude;
    private Double longitude;
    private String address;

    @ManyToOne
    @JoinColumn(name="annonce_id")
    @JsonBackReference // Indicates this is the child side
    private Annonce annonce;

    @ManyToOne
    @JoinColumn(name = "id_reservation")
    @JsonIgnore
    private Reservation reservation;
}
