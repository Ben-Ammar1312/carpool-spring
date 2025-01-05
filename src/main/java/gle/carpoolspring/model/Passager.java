package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("PASSAGER")
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idUser")
public class Passager extends User {



    @OneToMany(mappedBy = "passager")
    private List<gle.carpoolspring.model.Avis> avis;


    @OneToMany(mappedBy = "passager")
    private List<Reservation> reservations;


}
