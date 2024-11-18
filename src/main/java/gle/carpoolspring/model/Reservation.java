package gle.carpoolspring.model;

import gle.carpoolspring.enums.Etat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id_reservation;
    private String date_reservation;
    private int nbrPlaces;

    @Enumerated(EnumType.STRING)
    private Etat etat;


    @ManyToOne
    @JoinColumn(name = "id_passager", nullable = false)
    private gle.carpoolspring.model.Passager passager;


    @ManyToOne
    @JoinColumn(name = "id_annonce", nullable = false)
    private gle.carpoolspring.model.Annonce annonce;

    @OneToOne
    @JoinColumn(name = "id_paiement")
    private gle.carpoolspring.model.Paiement paiement;

}
