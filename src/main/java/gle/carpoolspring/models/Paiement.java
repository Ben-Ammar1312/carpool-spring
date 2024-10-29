package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_paiement;
    String date_paiement;
    Float montant;

    @Enumerated(EnumType.STRING)
    Etat etat;

    @Enumerated(EnumType.STRING)
    gle.carpoolspring.models.Mode mode;

    @OneToOne(mappedBy = "paiements")
    private Reservation reservations;

}
