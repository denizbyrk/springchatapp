package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.repository.UserRepository;
import com.chatapp.chat_backend.service.FriendRequestService;
import com.chatapp.chat_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/friends")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // eksikti

    @PostMapping("/request")
    public String sendFriendRequest(@RequestParam Long toUserId,
                                    RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();

        User sender = userRepository.findByEmail(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Optional<User> optionalReceiver = userRepository.findById(toUserId);
        if (optionalReceiver.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ User not found for ID: " + toUserId);
            return "redirect:/friends/list";
        }

        User receiver = optionalReceiver.get();

        // ❗ Kullanıcı kendine istek gönderemez
        if (sender.getId().equals(receiver.getId())) {
            redirectAttributes.addFlashAttribute("error", "❌ You cannot send a friend request to yourself!");
            return "redirect:/friends/list";
        }

        // ❗ Daha önce gönderilmiş ve reddedilmemiş bir istek varsa engelle
        if (friendRequestService.isRequestAlreadySent(sender, receiver)) {
            redirectAttributes.addFlashAttribute("error", "❌ Friend request already sent or pending/accepted.");
            return "redirect:/friends/list";
        }

        friendRequestService.sendFriendRequest(sender, receiver);

        redirectAttributes.addFlashAttribute("message", "✅ Friend request sent to user ID " + toUserId + "!");
        return "redirect:/friends/list";
    }

    @GetMapping("/pending")
    public String viewPendingRequests(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Sadece status = "PENDING" olanları al
        List<FriendRequest> pendingRequests = friendRequestService.findPendingForUser(currentUser);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);
        return "pending-requests"; // pending-requests.html
    }


    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam("requestId") Long requestId) {
    	System.out.println(requestId);
    	
        FriendRequest request = friendRequestService.findById(requestId);
        friendRequestService.acceptRequest(request);
        return "redirect:/friends/pending";
    }

    @PostMapping("/decline")
    public String declineFriendRequest(@RequestParam("requestId") Long requestId) {
        FriendRequest request = friendRequestService.findById(requestId);
        friendRequestService.declineRequest(request);
        return "redirect:/friends/pending";
    }
    
    @PostMapping("/remove")
    public String removeFriend(@RequestParam("friendId") Long friendId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        String currentUsername = principal.getName();
        userService.removeFriendship(currentUsername, friendId);
        redirectAttributes.addFlashAttribute("message", "Removed from friend list.");
        return "redirect:/friends/list";
    }
}
