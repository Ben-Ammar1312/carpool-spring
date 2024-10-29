package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_avis;
    float note;


    @ManyToOne
    @JoinColumn(name = "id_conducteur", nullable = false)
    private Conducteur conducteur;

    // Many avis can be given by one passager
    @ManyToOne
    @JoinColumn(name = "id_passager", nullable = false)
    private Passager passagers;

}
