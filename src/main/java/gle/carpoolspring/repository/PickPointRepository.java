package gle.carpoolspring.repository;

import gle.carpoolspring.model.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PickPointRepository extends JpaRepository<PickupPoint, Integer> {
    @Query("select p from PickupPoint p where p.annonce.idAnnonce = :annonceId")
    List<PickupPoint> findByAnnonceId(@Param("annonceId") Integer pickupId);


}
