package gle.carpoolspring.model;

import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.enums.Mode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_paiement;
    private String date_paiement;
    private Float montant;

    @Enumerated(EnumType.STRING)
    private Etat etat;

    @Enumerated(EnumType.STRING)
    private Mode mode;

    @OneToOne(mappedBy = "paiement")
    private Reservation reservation;

}
