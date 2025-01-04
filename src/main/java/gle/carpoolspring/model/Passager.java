package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("PASSAGER")
@Getter
@Setter
public class Passager extends User {



    @OneToMany(mappedBy = "passager")
    private List<gle.carpoolspring.model.Avis> avis;


    @OneToMany(mappedBy = "passager")
    private List<Reservation> reservations;


}
