package gle.carpoolspring.model;

import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.enums.Mode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_paiement;
    private LocalDateTime date_paiement;
    private Float montant;

    @Enumerated(EnumType.STRING)
    private Etat etat;

    @Enumerated(EnumType.STRING)
    private Mode mode;

    private String paymentIntentId;
    @OneToOne(mappedBy = "paiement")
    private Reservation reservation;

}
