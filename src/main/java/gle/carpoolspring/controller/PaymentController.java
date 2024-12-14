package gle.carpoolspring.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import gle.carpoolspring.dto.PaymentRequest;
import gle.carpoolspring.dto.RefundRequest;
import gle.carpoolspring.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Endpoint to create a Payment Intent
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentRequest.getAmount(), paymentRequest.getCurrency());
            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            // Handle exception (log it and return an appropriate response)
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // New endpoint to create a refund
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

    // Optional: Endpoint to retrieve a refund
    @GetMapping("/refunds/{refundId}")
    public ResponseEntity<?> getRefund(@PathVariable String refundId) {
        try {
            Refund refund = paymentService.retrieveRefund(refundId);
            return ResponseEntity.ok(refund);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Optional: Endpoint to handle Stripe webhooks
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        // Implement webhook handling logic
        // Verify signature, parse event, and handle accordingly
        return ResponseEntity.ok("Received");
    }
}