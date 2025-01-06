package gle.carpoolspring.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import gle.carpoolspring.dto.PaymentRequest;
import gle.carpoolspring.dto.RefundRequest;
import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.model.Reservation;
import gle.carpoolspring.model.Paiement;
import gle.carpoolspring.repository.ReservationRepository;
import gle.carpoolspring.repository.PaiementRepository;
import gle.carpoolspring.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaiementRepository paiementRepository;


    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentRequest.getAmount(), paymentRequest.getCurrency());
            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            // Log the exception (consider using a logging framework)
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/confirm-payment")
    @Transactional
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, String> request) {
        String paymentIntentId = request.get("paymentIntentId");
        if (paymentIntentId == null || paymentIntentId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment Intent ID is required."));
        }

        try {
            // Retrieve PaymentIntent from Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            // Check PaymentIntent status
            String status = paymentIntent.getStatus().toLowerCase();

            // Retrieve Paiement entity from DB
            Paiement paiement = paiementRepository.findByPaymentIntentId(paymentIntentId);
            if (paiement == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Paiement not found."));
            }

            // Update payment status based on PaymentIntent status
            if ("succeeded".equals(status)) {
                paiement.setEtat(Etat.SUCCESS);
                paiementRepository.save(paiement);
                return ResponseEntity.ok(Map.of("message", "Payment successful."));
            } else if ("requires_payment_method".equals(status) || "requires_confirmation".equals(status)) {
                paiement.setEtat(Etat.PENDING);
                paiementRepository.save(paiement);
                return ResponseEntity.ok(Map.of("message", "Payment is pending."));
            } else {
                paiement.setEtat(Etat.FAILED);
                paiementRepository.save(paiement);
                return ResponseEntity.status(400).body(Map.of("error", "Payment failed."));
            }

        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to create a refund
     *
     * @param refundRequest Contains paymentIntentId
     * @return Refund details or error
     */
    @PostMapping("/create-refund")
    public ResponseEntity<Map<String, Object>> createRefund(@RequestBody RefundRequest refundRequest) {
        try {
            Refund refund = paymentService.createRefund(refundRequest.getPaymentIntentId());
            return ResponseEntity.ok(Map.of(
                    "refundId", refund.getId(),
                    "status", refund.getStatus(),
                    "amount", refund.getAmount()
            ));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to retrieve a refund
     *
     * @param refundId The Refund ID
     * @return Refund details or error
     */
    @GetMapping("/refunds/{refundId}")
    public ResponseEntity<?> getRefund(@PathVariable String refundId) {
        try {
            Refund refund = paymentService.retrieveRefund(refundId);
            return ResponseEntity.ok(refund);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Optional: Endpoint to handle Stripe webhooks
     *
     * @param payload   The request payload
     * @param sigHeader The Stripe signature header
     * @return Confirmation message
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        // Implement webhook handling logic if needed
        return ResponseEntity.ok("Received");
    }
}