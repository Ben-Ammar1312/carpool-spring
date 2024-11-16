package gle.carpoolspring.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Passager extends User {



    @OneToMany(mappedBy = "passager")
    private List<gle.carpoolspring.models.Avis> avis;


    @OneToMany(mappedBy = "passager")
    private List<Reservation> reservations;


}
