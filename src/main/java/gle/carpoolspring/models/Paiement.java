package gle.carpoolspring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    Etat status;
    Mode mode;
}
