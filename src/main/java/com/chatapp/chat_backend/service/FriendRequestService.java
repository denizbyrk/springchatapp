package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.repository.FriendRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public FriendRequest sendFriendRequest(User sender, User receiver) {
        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status("PENDING")
                .build();
        return friendRequestRepository.save(request);
    }

    public List<FriendRequest> getReceivedRequests(User receiver) {
        return friendRequestRepository.findByReceiver(receiver);
    }

    public List<FriendRequest> getAcceptedFriends(User user) {
        List<FriendRequest> sent = friendRequestRepository.findBySenderAndStatus(user, "ACCEPTED");
        List<FriendRequest> received = friendRequestRepository.findByReceiverAndStatus(user, "ACCEPTED");
        sent.addAll(received);
        return sent;
    }

    public FriendRequest findById(Long id) {
        return friendRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arkadaşlık isteği bulunamadı"));
    }

    public void acceptRequest(FriendRequest request) {
        request.setStatus("ACCEPTED");
        friendRequestRepository.save(request);
    }

    public void declineRequest(FriendRequest request) {
        friendRequestRepository.delete(request);
    }
    public List<FriendRequest> getSentPendingRequests(User sender) {
        return friendRequestRepository.findBySenderAndStatus(sender, "PENDING");
    }
    public List<FriendRequest> findPendingForUser(User receiver) {
        return friendRequestRepository.findByReceiverAndStatus(receiver, "PENDING");
    }
    public boolean isRequestAlreadySent(User sender, User receiver) {
        return friendRequestRepository.existsBySenderAndReceiverAndStatusNot(sender, receiver, "REJECTED")
            || friendRequestRepository.existsBySenderAndReceiverAndStatusNot(receiver, sender, "REJECTED");
    }

}
