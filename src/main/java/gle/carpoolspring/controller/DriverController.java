package gle.carpoolspring.controller;

import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.enums.Status;
import gle.carpoolspring.model.*;
import gle.carpoolspring.repository.PickPointRepository;
import gle.carpoolspring.repository.WaypointRepository;
import gle.carpoolspring.repository.WaypointSuggestionRepository;
import gle.carpoolspring.service.AnnonceService;
import gle.carpoolspring.service.ReservationService;
import gle.carpoolspring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/driver")
@PreAuthorize("hasRole('DRIVER')")
public class DriverController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private AnnonceService annonceService;
    @Autowired
    private WaypointSuggestionRepository waypointSuggestionRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PickPointRepository pickPointRepository;

    @PostMapping("/waypoint/{suggestionId}/approve")
    public ResponseEntity<?> approveWaypoint(@PathVariable int suggestionId, Principal principal) {
        String email = principal.getName();
        Conducteur driver = (Conducteur) userService.findByEmail(email);

        WaypointSuggestion suggestion = waypointSuggestionRepository.findById(suggestionId)
                .orElse(null);

        if (suggestion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Suggestion not found");
        }

        Annonce annonce = suggestion.getAnnonce();
        if (annonce == null || annonce.getConducteur().getIdUser() != driver.getIdUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Mark the suggestion as approved
        suggestion.setApprovedByDriver(true);
        waypointSuggestionRepository.save(suggestion);
        Reservation reservation = suggestion.getReservation();
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No associated reservation found");
        }
        // Add the suggested waypoint to the annonce route
        Waypoint newWaypoint = new Waypoint();
        newWaypoint.setLatitude(suggestion.getLatitude());
        newWaypoint.setLongitude(suggestion.getLongitude());
        newWaypoint.setAddress(suggestion.getAddress());
        newWaypoint.setAnnonce(annonce);
        newWaypoint.setReservation(reservation);


        PickupPoint newPickupPoint = new PickupPoint();
        newPickupPoint.setLatitude(suggestion.getLatitude());
        newPickupPoint.setLongitude(suggestion.getLongitude());
        newPickupPoint.setAddress(suggestion.getAddress());
        newPickupPoint.setAnnonce(annonce);

// Save pickup point
        pickPointRepository.save(newPickupPoint);

        reservation.getWaypoints().add(newWaypoint);
        reservation.getWaypointSuggestions().remove(suggestion);
        annonce.setNbrPlaces(annonce.getNbrPlaces() - reservation.getNbrPlaces());
        if (annonce.getNbrPlaces() == 0) {
            annonce.setStatus(Status.Full);
        }
        annonceService.saveAnnonce(annonce);
       reservation.setAnnonce(annonce);
       reservation.setEtat(Etat.VALIDE);
       reservationService.save(reservation);
        User passenger = reservation.getPassager();
        int passengerId = passenger.getIdUser();

// Construct a small DTO or even just a text message
        Map<String, Object> notifPayload = new HashMap<>();
        notifPayload.put("message", "Your waypoint suggestion has been approved by the driver.");
        notifPayload.put("reservationId", reservation.getId_reservation());

// Send it to the passenger’s specific notification channel
        messagingTemplate.convertAndSend("/topic/notifications/" + passengerId, notifPayload);

        return ResponseEntity.ok("Waypoint approved and route updated");
    }

    @PostMapping("/waypoint/{suggestionId}/reject")
    public ResponseEntity<?> rejectWaypoint(@PathVariable int suggestionId, Principal principal) {
        String email = principal.getName();
        Conducteur driver = (Conducteur) userService.findByEmail(email);

        WaypointSuggestion suggestion = waypointSuggestionRepository.findById(suggestionId)
                .orElse(null);

        if (suggestion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Suggestion not found");
        }

        Annonce annonce = suggestion.getAnnonce();
        if (annonce == null || annonce.getConducteur().getIdUser() != driver.getIdUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Mark as rejected
        suggestion.setRejected(true);
        waypointSuggestionRepository.save(suggestion);

        Reservation reservation = suggestion.getReservation();
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No associated reservation found");
        }

        reservation.getWaypointSuggestions().remove(suggestion);
        reservationService.save(reservation);
        return ResponseEntity.ok("Waypoint suggestion rejected");
    }


}