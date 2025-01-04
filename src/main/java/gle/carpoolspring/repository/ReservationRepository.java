package gle.carpoolspring.repository;

import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("select r from Reservation r where r.passager.idUser = :userId")
    List<Reservation> findByPassagerId(@Param("userId") int userId);


    @Query("SELECT r FROM Reservation r WHERE r.id_reservation = :id")
    Reservation findByIdReservation(@Param("id") int id);

    @Query("SELECT r FROM Reservation r WHERE r.annonce.idAnnonce = :annonceId AND r.passager.idUser = :passagerId")
    Reservation findByAnnonceIdAndPassagerId(@Param("annonceId") int annonceId, @Param("passagerId") int passagerId);
    List<Reservation> findByPassager_IdUserAndEtat(int passagerId, Etat etat);
    // Custom query methods if needed
}