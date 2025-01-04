package gle.carpoolspring.controller;

import gle.carpoolspring.model.User;
import gle.carpoolspring.service.MessageService;
import gle.carpoolspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class PaymentPageController {

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @GetMapping("/payment")
    public String getPaymentPage  ( @RequestParam(required=false) Double price,
    @RequestParam(required=false) Integer rideId,
    @RequestParam(required=false) Integer reservationId,Model model, Principal principal) {
        model.addAttribute("price", price);
        model.addAttribute("stripePublishableKey", stripePublishableKey);
        model.addAttribute("paymentStatus", ""); // Initialize payment status
        User currentUser = userService.findByEmail(principal.getName());
        int unreadCount = messageService.countUnreadMessagesForUser(currentUser.getIdUser());
        model.addAttribute("unreadMessages", unreadCount);
        model.addAttribute("currentUser", currentUser);
        return "payment";
    }


    // New endpoint to serve the refund management page
    @GetMapping("/manage-refund")
    public String getManageRefundPage(Model model) {
        return "manage-refund";
    }
}