package com.chatapp.chat_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.Message;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.repository.FriendRequestRepository;
import com.chatapp.chat_backend.repository.MessageRepository;
import com.chatapp.chat_backend.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }
    
    public List<User> findAllExcludingCurrent(Long currentUserId) {
        List<User> allUsers = userRepository.findAll();
        allUsers.removeIf(user -> user.getId().equals(currentUserId));
        return allUsers;
    }
    
    public List<User> findFriends(User currentUser) {
        List<User> friends = new ArrayList<>();
        List<FriendRequest> sent = friendRequestRepository.findBySenderAndStatus(currentUser, "ACCEPTED");
        List<FriendRequest> received = friendRequestRepository.findByReceiverAndStatus(currentUser, "ACCEPTED");
        
        sent.forEach(r -> friends.add(r.getReceiver()));
        received.forEach(r -> friends.add(r.getSender()));
        
        return friends;
    }
    
    public void removeFriendship(String currentUsername, Long friendId) {
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Giriş yapan kullanıcı bulunamadı"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Arkadaş bulunamadı"));
        
        List<FriendRequest> requests = friendRequestRepository
                .findBySenderAndReceiverOrReceiverAndSender(currentUser, friend, currentUser, friend);
        
        if (!requests.isEmpty()) {
            friendRequestRepository.deleteAll(requests);
        } else {
            throw new RuntimeException("Arkadaşlık ilişkisi bulunamadı.");
        }
    }
    
    /**
     * GÜNCELLENMIŞ DELETE METODU
     * Kullanıcıyı ve tüm ilişkili verilerini güvenli şekilde siler
     */
    @Transactional
    public void delete(User user) {
        try {
            // 1. Kullanıcının tüm mesajlarını sil (hem gönderdiği hem aldığı)
            messageRepository.deleteAllByUserId(user.getId());
            
            // 2. Arkadaşlık isteklerini sil
            friendRequestRepository.deleteAllBySender(user);
            friendRequestRepository.deleteAllByReceiver(user);
            
            // 3. Son olarak kullanıcıyı sil
            userRepository.delete(user);
            
        } catch (Exception e) {
            throw new RuntimeException("Kullanıcı silinirken hata oluştu: " + e.getMessage(), e);
        }
    }
    
    /**
     * ALTERNATİF: Liste tabanlı silme (daha kontrollü)
     */
    @Transactional
    public void deleteAlternative(User user) {
        try {
            // 1. Kullanıcının tüm mesajlarını listele ve sil
            List<Message> allMessages = messageRepository.findAllByUser(user);
            if (!allMessages.isEmpty()) {
                messageRepository.deleteAll(allMessages);
            }
            
            // 2. Arkadaşlık isteklerini sil
            friendRequestRepository.deleteAllBySender(user);
            friendRequestRepository.deleteAllByReceiver(user);
            
            // 3. Son olarak kullanıcıyı sil
            userRepository.delete(user);
            
        } catch (Exception e) {
            throw new RuntimeException("Kullanıcı silinirken hata oluştu: " + e.getMessage(), e);
        }
    }
    
    /**
     * Silme öncesi istatistik bilgisi (isteğe bağlı)
     */
    public String getDeletionInfo(User user) {
        int totalMessages = messageRepository.findAllByUser(user).size();
        int sentMessages = messageRepository.findAllBySender(user).size();
        int receivedMessages = messageRepository.findAllByReceiver(user).size();
        
        return String.format("Silinecek: %d toplam mesaj (%d gönderilen, %d alınan)", 
                           totalMessages, sentMessages, receivedMessages);
    }
}