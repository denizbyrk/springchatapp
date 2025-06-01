package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.model.Message;
import com.chatapp.chat_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    
    List<Message> findAllBySender(User sender);
    
    List<Message> findAllByReceiver(User receiver);
    
    
    @Query("SELECT m FROM Message m WHERE m.sender = :user OR m.receiver = :user")
    List<Message> findAllByUser(@Param("user") User user);
    
    // YENİ: Kullanıcı ID'sine göre mesajları sil
    @Modifying
    @Query("DELETE FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
    
    
 // En son mesajı (her iki kullanıcı arasında)
    Optional<Message> findTopBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampDesc(
            Long senderId1, Long receiverId1, Long receiverId2, Long senderId2
        );
    
    
    
}
