package gle.carpoolspring.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Passager extends User {



    @OneToMany(mappedBy = "passager")
    private List<gle.carpoolspring.model.Avis> avis;


    @OneToMany(mappedBy = "passager")
    private List<Reservation> reservations;


}
