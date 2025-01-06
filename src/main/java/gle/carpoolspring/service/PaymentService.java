package gle.carpoolspring.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import gle.carpoolspring.enums.Etat;
import gle.carpoolspring.enums.Mode;
import gle.carpoolspring.model.Paiement;
import gle.carpoolspring.repository.PaiementRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String secretKey;


    @Autowired
    private PaiementRepository paiementRepository;
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }


    public PaymentIntent createPaymentIntent(long amount, String currency) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .build();

        // Create Payment Intent with Stripe
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Save payment details to the database
        Paiement paiement = new Paiement();
        paiement.setMontant((float) amount / 100); // Convert cents to dollars
        paiement.setDate_paiement(LocalDateTime.now());
        paiement.setEtat(Etat.PAYMENT_PENDING); // Assuming PENDING is an enum value representing an ongoing payment
        paiement.setMode(Mode.EN_LIGNE);
        paiement.setPaymentIntentId(paymentIntent.getId());

        paiementRepository.save(paiement);

        return paymentIntent;
    }
    // New method to create a refund
    public Refund createRefund(String paymentIntentId) throws StripeException {
        RefundCreateParams params =
                RefundCreateParams.builder()
                        .setPaymentIntent(paymentIntentId)
                        .build();
        // Create Refund with Stripe
        Refund refund = Refund.create(params);

        // Update payment status in the database
        Paiement paiement = paiementRepository.findByPaymentIntentId(paymentIntentId);
        if (paiement != null) {
            paiement.setEtat(Etat.REFUNDED); // Assuming REFUNDED is an enum value
            paiementRepository.save(paiement);
        }

        return refund;
    }

    // Optional: Retrieve a refund
    public Refund retrieveRefund(String refundId) throws StripeException {
        return Refund.retrieve(refundId);
    }

}