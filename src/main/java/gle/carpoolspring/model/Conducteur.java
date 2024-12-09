package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
public class Conducteur extends User{


    @OneToMany(mappedBy = "conducteur")
    private List<Voiture> voitures;


    @OneToMany(mappedBy = "conducteur" ,fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<gle.carpoolspring.model.Annonce> annonces;


    @OneToMany(mappedBy = "conducteur")
    private List<gle.carpoolspring.model.Avis> avis;


}
