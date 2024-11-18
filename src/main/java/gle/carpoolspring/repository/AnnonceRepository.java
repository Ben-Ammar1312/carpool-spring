package gle.carpoolspring.repository;

import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    //  List<Annonce> findByConducteur_IdUser(int idUser);
    List<Annonce> findByConducteur(Conducteur conducteur);

}

    @Query("SELECT a FROM Annonce a WHERE " +
            "(:lieuDepart IS NULL OR a.lieuDepart LIKE %:lieuDepart%) AND " +
            "(:lieuArrivee IS NULL OR a.lieuArrivee LIKE %:lieuArrivee%) AND " +
            "(:date_depart IS NULL OR a.date_depart = :date_depart) AND " +
            "(:nbrPlaces IS NULL OR a.nbrPlaces >= :nbrPlaces) AND " +
            "(:maxPrice IS NULL OR a.prix <= :maxPrice)")
    List<Annonce> searchRides(@Param("lieuDepart") String lieuDepart,
                              @Param("lieuArrivee") String lieuArrivee,
                              @Param("date_depart") String dateDepart,
                              @Param("nbrPlaces") Integer nbrPlaces,
                              @Param("maxPrice") Float maxPrice);