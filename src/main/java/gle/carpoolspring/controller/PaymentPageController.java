package gle.carpoolspring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentPageController {

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    @GetMapping("/payment")
    public String getPaymentPage(Model model) {
        model.addAttribute("stripePublishableKey", stripePublishableKey);
        model.addAttribute("paymentStatus", ""); // Initialize payment status
        return "payment";
    }


    // New endpoint to serve the refund management page
    @GetMapping("/manage-refund")
    public String getManageRefundPage(Model model) {
        return "manage-refund";
    }
}