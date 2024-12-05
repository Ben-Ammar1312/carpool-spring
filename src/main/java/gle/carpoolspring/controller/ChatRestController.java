package gle.carpoolspring.controller;

import gle.carpoolspring.model.Message;
import gle.carpoolspring.service.MessageService;
import gle.carpoolspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping("/messages/{userId}")
    public List<Message> getMessages(@PathVariable("userId") int userId) {
        return messageService.getMessagesForUser(userId);
    }
}

