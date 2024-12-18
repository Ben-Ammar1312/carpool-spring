package gle.carpoolspring.controller;

import gle.carpoolspring.model.Message;
import gle.carpoolspring.model.User;
import gle.carpoolspring.service.LinkedUserService;
import gle.carpoolspring.service.MessageService;
import gle.carpoolspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private LinkedUserService linkedUserService;

    @GetMapping
    public String showMessages(Model model, Principal principal) {
        User loggedInUser = userService.findByEmail(principal.getName());
        int userId = loggedInUser.getIdUser();

        // Retrieve Linked Users With The Logged User
        List<Integer> linkeduserslist = linkedUserService.getLinkedUsersIds(userId);

        List<Message> messages = new ArrayList<>();

        for (Integer linkeduser : linkeduserslist) {
            messages.addAll(messageService.getConversationMessages(userId, linkeduser.intValue()));
        }

        // Retrieve distinct discussions
        //List<Message> messages = messageService.getMessagesForUser(userId);

        // Group messages by conversation partner
        Map<Integer, List<Message>> conversations = messages.stream()
                .collect(Collectors.groupingBy(message ->
                        message.getSender().getIdUser() == userId
                                ? message.getReceiver().getIdUser()
                                : message.getSender().getIdUser()
                ));

        // Prepare conversation details
        Map<String, List<Message>> conversationDetails = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<Message>> entry : conversations.entrySet()) {
            User partner = userService.findById(entry.getKey());
            String partnerName = partner.getPrenom() + " " + partner.getNom();
            conversationDetails.put(partnerName, entry.getValue());
        }

        model.addAttribute("conversations", conversationDetails);
        return "chat"; // Points to the Thymeleaf template
    }

    @PostMapping("/send")
    public String sendMessage(
            Principal principal,
            @RequestParam("receiverId") int receiverId,
            @RequestParam("content") String content
    ) {

        User sender = userService.findByEmail(principal.getName());
        User receiver = userService.findById(receiverId);

        if (sender == null || receiver == null) {
            return "redirect:/chat?error=invalid_users";
        }

        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setContent_message(content);

        // Use modern date-time API
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        newMessage.setDate_message(formattedDate);

        messageService.saveMessage(newMessage);

        return "redirect:/chat";
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/view")
    public ResponseEntity<Void> viewConversation(Principal principal,
                                                 @RequestBody Map<String, Object> request) {
        // Read the content of the request and transform the id value to an integer
        Object partnerIdObj = request.get("partnerId");

        if (partnerIdObj != null) {
            try {
                User loggedInUser = userService.findByEmail(principal.getName());
                int userId = loggedInUser.getIdUser();
                // Transform the id value to an integer
                int partnerId = Integer.parseInt(partnerIdObj.toString());

                List<Message> messages = messageService.getUnreadMessages(partnerId, userId);

                for (Message message : messages) {
                    message.setRead(true);
                    messageService.saveMessage(message); // Save the updated message
                }

            } catch (NumberFormatException e) {
                System.err.println("Invalid partnerId: " + partnerIdObj);
            }
        } else {
            System.err.println("partnerId is missing in the request.");
        }

        return ResponseEntity.ok().build();
    }







}





