package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.service.FriendRequestService;
import com.chatapp.chat_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRequestService friendRequestService;
    
    @GetMapping("/list")
    public String viewFriends(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<User> friends = userService.findFriends(currentUser);        
        List<User> users = userService.findAllExcludingCurrent(currentUser.getId());
        List<User> pendingRequests = friendRequestService.getSentPendingRequests(currentUser)
                                                        .stream()
                                                        .map(FriendRequest::getReceiver)
                                                        .toList();

        model.addAttribute("users", users);
        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);
        

        return "friends"; // src/main/resources/templates/friends.html olmalı
    }
}
