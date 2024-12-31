package gle.carpoolspring.repository;

import gle.carpoolspring.model.Annonce;
import gle.carpoolspring.model.PickupPoint;
import gle.carpoolspring.model.WaypointSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaypointSuggestionRepository extends JpaRepository<WaypointSuggestion, Integer> {
    @Query("select p from WaypointSuggestion p where p.annonce.idAnnonce = :annonceId")
    List<WaypointSuggestion> findByAnnonceId(@Param("annonceId") Integer pickupId);

}
