package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiver(User receiver);
    List<FriendRequest> findBySenderAndStatus(User sender, String status);
    List<FriendRequest> findByReceiverAndStatus(User receiver, String status);
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
    	       "(fr.sender = :user1 AND fr.receiver = :user2) OR " +
    	       "(fr.sender = :user2 AND fr.receiver = :user1)")
    	List<FriendRequest> findBySenderAndReceiverOrReceiverAndSender(
    	        @Param("user1") User user1,
    	        @Param("user2") User user2,
    	        @Param("user2") User user2b,
    	        @Param("user1") User user1b
    	);

    
    void deleteAllBySender(User sender);
    void deleteAllByReceiver(User receiver);
    
    boolean existsBySenderAndReceiverAndStatusNot(User sender, User receiver, String status);

}
