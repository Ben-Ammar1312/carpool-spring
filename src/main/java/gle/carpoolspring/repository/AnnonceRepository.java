package gle.carpoolspring.repository;


import gle.carpoolspring.enums.Status;
import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.Conducteur;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    //  List<Annonce> findByConducteur_IdUser(int idUser);
    @EntityGraph(attributePaths = {"pickupPoints", "waypoints", "reservations", "waypointSuggestions"})
    List<Annonce> findByConducteur(Conducteur conducteur);

    @EntityGraph(attributePaths = {"pickupPoints", "waypoints", "reservations", "waypointSuggestions"})
    List<Annonce> findAll();

    @EntityGraph(attributePaths = {"pickupPoints", "waypoints", "reservations", "waypointSuggestions"})
    @Query("SELECT a FROM Annonce a WHERE " +
            "(:lieuDepart IS NULL OR a.lieuDepart LIKE %:lieuDepart%) AND " +
            "(:lieuArrivee IS NULL OR a.lieuArrivee LIKE %:lieuArrivee%) AND " +
            "(:dateDepart IS NULL OR a.dateDepart = :dateDepart) AND " +
            "(:nbrPlaces IS NULL OR a.nbrPlaces >= :nbrPlaces) AND " +
            "(:maxPrice IS NULL OR a.prix <= :maxPrice)")
    List<Annonce> searchRides(@Param("lieuDepart") String lieuDepart,
                              @Param("lieuArrivee") String lieuArrivee,
                              @Param("dateDepart") String dateDepart,
                              @Param("nbrPlaces") Integer nbrPlaces,
                              @Param("maxPrice") Float maxPrice);



    Optional<Annonce> findById(Integer idAnnonce);

    long countByStatus(Status status);
}
