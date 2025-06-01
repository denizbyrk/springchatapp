//package com.chatapp.chat_backend.controller;
//
//import com.chatapp.chat_backend.model.Message;
//import com.chatapp.chat_backend.model.User;
//import com.chatapp.chat_backend.service.MessageService;
//import com.chatapp.chat_backend.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketMessageController {
//
//    @Autowired
//    private MessageService messageService;
//
//    @Autowired
//    private UserService userService;
//
//    @MessageMapping("/chat")
////    @SendToUser("/queue/messages")
//    public Message handleMessage(Message message) {
//        // Sender and receiver must be already set on message
//        User sender = userService.findById(message.getSender().getId());
//        User receiver = userService.findById(message.getReceiver().getId());
//
//        // Save to DB
//        messageService.sendMessage(sender, receiver, message.getContent());
//
//        return message; // This will be sent to the user via WebSocket
//    }
//}
