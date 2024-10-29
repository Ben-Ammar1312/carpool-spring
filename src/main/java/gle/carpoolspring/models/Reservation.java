package gle.carpoolspring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    int id_reservation;
    String date_reservation;
    int nbrPlaces;

    @Enumerated(EnumType.STRING)
    Etat etat;


    @ManyToOne
    @JoinColumn(name = "id_passager", nullable = false)
    private gle.carpoolspring.models.Passager passagers;


    @ManyToOne
    @JoinColumn(name = "id_annonce", nullable = false)
    private gle.carpoolspring.models.Annonce annonces;

    @OneToOne
    @JoinColumn(name = "id_paiement")
    private gle.carpoolspring.models.Paiement paiements;

}
