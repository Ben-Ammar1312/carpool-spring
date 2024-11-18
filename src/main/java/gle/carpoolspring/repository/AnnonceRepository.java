package gle.carpoolspring.repository;

import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    //  List<Annonce> findByConducteur_IdUser(int idUser);
    List<Annonce> findByConducteur(Conducteur conducteur);

}
