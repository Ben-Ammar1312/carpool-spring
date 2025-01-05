package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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


    @OneToMany(mappedBy = "conducteur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Voiture> voitures;


    @OneToMany(mappedBy = "conducteur", fetch= FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference // Indicates this is the child side
    private List<gle.carpoolspring.model.Annonce> annonces;


    @OneToMany(mappedBy = "conducteur", fetch= FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<gle.carpoolspring.model.Avis> avis;


}
