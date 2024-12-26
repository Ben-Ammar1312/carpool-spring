package gle.carpoolspring.repository;

import gle.carpoolspring.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Integer> {



    @Query("SELECT COALESCE(AVG(a.note), 0 )FROM Avis a WHERE a.conducteur.idUser = :driverId AND a.avisType = 'PASSENGER_TO_DRIVER'")
            float findAverageRatingForDriver(@Param("driverId") int driverId);

    @Query("SELECT COALESCE(AVG(a.note), 0 )FROM Avis a WHERE a.passager.idUser = :passengerId AND a.avisType = 'DRIVER_TO_PASSENGER'")
            float findAverageRatingForPassenger(@Param("passengerId") int passengerId);
}