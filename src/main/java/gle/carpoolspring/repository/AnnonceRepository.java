package gle.carpoolspring.repository;

import gle.carpoolspring.models.Annonce;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends CrudRepository<Annonce, Integer> {

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


}
