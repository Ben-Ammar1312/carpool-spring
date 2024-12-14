package gle.carpoolspring.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String secretKey;

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

        return PaymentIntent.create(params);
    }
    // New method to create a refund
    public Refund createRefund(String paymentIntentId) throws StripeException {
        RefundCreateParams params =
                RefundCreateParams.builder()
                        .setPaymentIntent(paymentIntentId)
                        .build();

        return Refund.create(params);
    }

    // Optional: Retrieve a refund
    public Refund retrieveRefund(String refundId) throws StripeException {
        return Refund.retrieve(refundId);
    }

}