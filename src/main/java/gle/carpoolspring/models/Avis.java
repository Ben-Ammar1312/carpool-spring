package gle.carpoolspring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

}
