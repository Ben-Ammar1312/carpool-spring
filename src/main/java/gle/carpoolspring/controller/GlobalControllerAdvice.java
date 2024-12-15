package gle.carpoolspring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.userdetails.UserDetails;
import gle.carpoolspring.model.User;
import gle.carpoolspring.service.UserService;
import gle.carpoolspring.service.MessageService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @ModelAttribute("unreadMessages")
    public int getUnreadMessages(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User loggedInUser = userService.findByEmail(userDetails.getUsername());
            int userId = loggedInUser.getIdUser();
            return messageService.countUnreadMessagesForUser(userId);
        }
        return 0;
    }
}

