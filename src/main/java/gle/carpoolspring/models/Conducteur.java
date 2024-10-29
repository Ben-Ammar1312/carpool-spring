package gle.carpoolspring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Conducteur extends User{


    @OneToMany(mappedBy = "conducteur")
    private List<Voiture> voitures;


    @OneToMany(mappedBy = "conducteur")
    private List<gle.carpoolspring.models.Annonce> annonces;


    @OneToMany(mappedBy = "conducteur")
    private List<gle.carpoolspring.models.Avis> avis;
}
