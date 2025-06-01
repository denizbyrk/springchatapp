package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.model.Message;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.service.MessageService;
import com.chatapp.chat_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String chatPage(@RequestParam("with") Long withUserId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {

        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        User otherUser = userService.findById(withUserId);

        List<Message> messages = messageService.getMessagesBetween(currentUser, otherUser);

        model.addAttribute("messages", messages);
        model.addAttribute("receiver", otherUser);
        model.addAttribute("currentUser", currentUser);

        return "chat"; // chat.html
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("receiverId") Long receiverId,
                               @RequestParam("content") String content,
                               @AuthenticationPrincipal UserDetails userDetails) {

        User sender = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        User receiver = userService.findById(receiverId);

        messageService.sendMessage(sender, receiver, content);

        return "redirect:/chat?with=" + receiverId;
    }
    
    @GetMapping("/messages/{receiverId}")
    @ResponseBody
    public List<Message> getMessagesJson(@PathVariable Long receiverId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        User receiver = userService.findById(receiverId);

        return messageService.getMessagesBetween(currentUser, receiver);
    }
  
}
