package gle.carpoolspring.model;

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
    private Annonce annonce;
}
