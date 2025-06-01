package com.chatapp.chat_backend.controller;

import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import com.chatapp.chat_backend.model.Message;
import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.repository.MessageRepository;
import com.chatapp.chat_backend.service.FriendRequestService;
import com.chatapp.chat_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRequestService friendRequestService;
    
    @Autowired
    private MessageRepository messageRepository;


    @GetMapping
    public String listUsers(Model model, @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(name = "search", required = false) String search) {

        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")); 

        List<User> users = userService.findAllExcludingCurrent(currentUser.getId());

        if (search != null && !search.isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        List<User> friends = userService.findFriends(currentUser);
        List<User> pendingRequests = friendRequestService.getSentPendingRequests(currentUser)
                                                        .stream()
                                                        .map(FriendRequest::getReceiver)
                                                        .toList();

        model.addAttribute("users", users);
        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);

     // Son mesajlar eklendi
        
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        Map<Long, Message> lastMessages = new HashMap<>();
        Map<Long, String> lastMessageTimes = new HashMap<>();

        for (User friend : friends) {
            messageRepository
                .findTopBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampDesc(
                        currentUser.getId(), friend.getId(),
                        currentUser.getId(), friend.getId())
                .ifPresent(msg -> {
                    lastMessages.put(friend.getId(), msg);
                    lastMessageTimes.put(friend.getId(), msg.getTimestamp().format(formatter));
                });
        }

        model.addAttribute("lastMessages", lastMessages);
        model.addAttribute("lastMessageTimes", lastMessageTimes);
        
        
        return "messages";
    }
    

}
