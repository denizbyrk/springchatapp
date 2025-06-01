package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.model.Message;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.service.MessageService;
import com.chatapp.chat_backend.service.UserService;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void send(Message message, Principal principal) {
        // Frontend'den sender ID'sini de alabiliriz
        User sender;
        
        if (principal != null) {
            // Önce Authentication ile deneyin
            sender = userService.findByUsername(principal.getName())
                    .orElse(null);
        } else {
            sender = null;
        }
        
        // Eğer Authentication'dan bulamazsak, message'dan sender ID'sini kullanın
        if (sender == null && message.getSender() != null && message.getSender().getId() != null) {
            sender = userService.findById(message.getSender().getId());
        }
        
        if (sender == null) {
            throw new RuntimeException("Cannot identify sender");
        }
        
        User receiver = userService.findById(message.getReceiver().getId());

        Message saved = messageService.sendMessage(sender, receiver, message.getContent());
        saved.setSender(sender);
        saved.setReceiver(receiver);

        // Chat room yaklaşımı - her ikisini de aynı odaya koy
        String chatRoom = "chat-" + Math.min(sender.getId(), receiver.getId()) + "-" + Math.max(sender.getId(), receiver.getId());
        
        // Bu chat room'daki herkese gönder
        messagingTemplate.convertAndSend("/topic/" + chatRoom, saved);
    }
}
