package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PickupPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Double latitude;
    private Double longitude;
    private String address;

    @ManyToOne
    @JoinColumn(name="id_annonce")
    private Annonce annonce;

}
