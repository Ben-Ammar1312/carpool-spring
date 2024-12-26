package gle.carpoolspring.model;

import gle.carpoolspring.enums.AvisType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_avis;
    private float note;

    @Enumerated(EnumType.STRING)
    private AvisType avisType;

    @ManyToOne
    @JoinColumn(name = "id_conducteur", nullable = false)
    private Conducteur conducteur;

    // Many avis can be given by one passager
    @ManyToOne
    @JoinColumn(name = "id_passager", nullable = false)
    private Passager passager;

}
