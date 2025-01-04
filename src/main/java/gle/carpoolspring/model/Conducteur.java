package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
@DiscriminatorValue("CONDUCTEUR")

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Conducteur extends User{


    @OneToMany(mappedBy = "conducteur")
    private List<Voiture> voitures;


    @OneToMany(mappedBy = "conducteur" ,fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<gle.carpoolspring.model.Annonce> annonces;


    @OneToMany(mappedBy = "conducteur")
    private List<gle.carpoolspring.model.Avis> avis;


}
