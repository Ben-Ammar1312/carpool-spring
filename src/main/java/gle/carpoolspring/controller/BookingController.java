package gle.carpoolspring.controller;


import gle.carpoolspring.dto.BookingRequest;
import gle.carpoolspring.dto.BookingResponse;
import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.enums.Status;
import gle.carpoolspring.model.*;
import gle.carpoolspring.repository.PickPointRepository;
import gle.carpoolspring.repository.WaypointSuggestionRepository;
import gle.carpoolspring.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
public class BookingController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private UserService userService;

    @Autowired
    private WaypointSuggestionRepository waypointSuggestionService;

    @Autowired
    private PickPointRepository pickPointService;
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/bookRide")
    @Transactional
    public ResponseEntity<?> bookRide(@RequestBody BookingRequest bookingRequest, Principal principal) {
        // Get the current user
        String email = principal.getName();
        Passager passager = (Passager) userService.findByEmail(email);

        // Get the annonce
        Annonce annonce = annonceService.getAnnonceById(bookingRequest.getAnnonceId());

        if (annonce == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BookingResponse("Annonce not found"));
        }
        if (annonce.getNbrPlaces() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BookingResponse("This ride is fully booked"));
        }
        int requestedSeats = 1; // Or get from bookingRequest if you allow booking multiple seats
        // Create a reservation
        Reservation reservation = new Reservation();
        reservation.setPassager(passager);
        reservation.setAnnonce(annonce);
        reservation.setNbrPlaces(requestedSeats);
        reservation.setDate_reservation(java.time.LocalDate.now().toString());
        if (bookingRequest.isOnRoute()) {
            if (annonce.getNbrPlaces() < requestedSeats) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BookingResponse("Not enough seats available"));
            }

            reservation.setEtat(Etat.VALIDE); // Automatically approved

            // Save reservation
            reservationService.save(reservation);
            annonce.setNbrPlaces(annonce.getNbrPlaces()-requestedSeats);
            if (annonce.getNbrPlaces() == 0) {
                annonce.setStatus(Status.Full); // Assuming you have a status field
            }
            annonceService.saveAnnonce(annonce);

            // Create pickup point
            PickupPoint pickupPoint = new PickupPoint();
            pickupPoint.setLatitude(bookingRequest.getPickupLat());
            pickupPoint.setLongitude(bookingRequest.getPickupLng());
            pickupPoint.setAnnonce(annonce);
            pickPointService.save(pickupPoint);
            // Save pickup point
            // Assume you have a service to handle this

            return ResponseEntity.ok(new BookingResponse("Booking confirmed"));
        } else {

            reservation.setEtat(Etat.EN_ATTENTE);
            // Create a WaypointSuggestion
            WaypointSuggestion suggestion = new WaypointSuggestion();
            suggestion.setLatitude(bookingRequest.getPickupLat());
            suggestion.setLongitude(bookingRequest.getPickupLng());
            suggestion.setReservation(reservation);
            suggestion.setAnnonce(annonce);
            reservation.getWaypointSuggestions().add(suggestion);
            reservationService.save(reservation);
            // Save suggestion
            // Assume you have a service to handle this


            // Notify driver
            // Implement your notification logic here

            return ResponseEntity.ok(new BookingResponse("Your request has been sent to the driver for approval"));
        }
    }

    @PostMapping("/cancelBooking")
    @Transactional
    public ResponseEntity<?> cancelBooking(@RequestBody Map<String, Integer> payload, Principal principal) {
        int annonceId = payload.get("annonceId");
        String email = principal.getName();
        User currentUser = userService.findByEmail(email);

        // Ensure the current user is a passenger
        if (!(currentUser instanceof Passager)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BookingResponse("Only passengers can cancel bookings."));
        }
        Passager passager = (Passager) currentUser;

        // Find the reservation
        Reservation reservation = reservationService.findByAnnonceIdAndPassagerId(annonceId, passager.getIdUser());
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BookingResponse("No booking found to cancel"));
        }

        // Get the associated annonce
        Annonce annonce = reservation.getAnnonce();

        // 1. Remove the reservation from annonce's reservations
        if (annonce.getReservations() != null) {
            annonce.getReservations().remove(reservation);
        }

        // 2. Remove the associated waypoints from annonce's waypoints list
        // Since reservation.getWaypoints() are going to be deleted, remove them from annonce first
        if (annonce.getWaypoints() != null && reservation.getWaypoints() != null) {
            annonce.getWaypoints().removeAll(reservation.getWaypoints());
        }

        // 3. Remove the associated waypoint suggestions from annonce's waypointSuggestions list
        if (annonce.getWaypointSuggestions() != null && reservation.getWaypointSuggestions() != null) {
            annonce.getWaypointSuggestions().removeAll(reservation.getWaypointSuggestions());
        }

        // 4. Nullify the reservation's association with annonce and passager
        reservation.setAnnonce(null);
        reservation.setPassager(null);

        // 5. For each Waypoint, nullify associations
        for (Waypoint waypoint : reservation.getWaypoints()) {
            waypoint.setAnnonce(null);
            waypoint.setReservation(null);
        }

        // 6. For each WaypointSuggestion, nullify associations
        for (WaypointSuggestion suggestion : reservation.getWaypointSuggestions()) {
            suggestion.setAnnonce(null);
            suggestion.setReservation(null);
        }

        // 7. Now, safely delete the reservation
        reservationService.delete(reservation);

        // 8. Update the available seats in the annonce
        annonce.setNbrPlaces(annonce.getNbrPlaces() + reservation.getNbrPlaces());

        // 9. Update the status if necessary
        if (annonce.getNbrPlaces() > 0 && annonce.getStatus() == Status.Full) {
            annonce.setStatus(Status.Open);
        }

        // 10. Save the updated annonce AFTER removing references
        annonceService.saveAnnonce(annonce);
        List<PickupPoint> pickupPointsForReservation = pickPointService.findByAnnonceId(annonceId);

        for (PickupPoint pp : pickupPointsForReservation) {
            pickPointService.delete(pp);
        }

        notificationService.createNotification(currentUser.getIdUser(), "Your booking has been succesfully cancelled ");

        return ResponseEntity.ok(new BookingResponse("Booking cancelled successfully"));
    }
}


