package gle.carpoolspring.service;

import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.model.Reservation;
import gle.carpoolspring.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }


    public Set<Integer> getBookedAnnonceIdsByUser(int userId) {
        List<Reservation> reservations = reservationRepository.findByPassager_IdUserAndEtat(userId, Etat.VALIDE);
        return reservations.stream()
                .map(reservation -> reservation.getAnnonce().getIdAnnonce())
                .collect(Collectors.toSet());
    }

    public Set<Integer> getPendingAnnonceIdsByUser(int userId) {
        List<Reservation> reservations = reservationRepository.findByPassager_IdUserAndEtat(userId, Etat.EN_ATTENTE);
        return reservations.stream()
                .map(reservation -> reservation.getAnnonce().getIdAnnonce())
                .collect(Collectors.toSet());
    }




    public Reservation findByAnnonceIdAndPassagerId(int annonceId, int passagerId) {
        return reservationRepository.findByAnnonceIdAndPassagerId(annonceId, passagerId);
    }


    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }


    // Other methods as needed
}