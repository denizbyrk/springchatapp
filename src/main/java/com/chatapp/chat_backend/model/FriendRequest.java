package com.chatapp.chat_backend.model;

import jakarta.persistence.*;

@Entity
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private String status = "PENDING"; // Varsayılan değer: Bekliyor

    // ----- Constructor -----
    public FriendRequest() {}

    // ----- Getter ve Setter'lar -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ----- Manual Builder -----
    public static FriendRequestBuilder builder() {
        return new FriendRequestBuilder();
    }

    public static class FriendRequestBuilder {
        private final FriendRequest friendRequest;

        public FriendRequestBuilder() {
            this.friendRequest = new FriendRequest();
        }

        public FriendRequestBuilder sender(User sender) {
            friendRequest.setSender(sender);
            return this;
        }

        public FriendRequestBuilder receiver(User receiver) {
            friendRequest.setReceiver(receiver);
            return this;
        }

        public FriendRequestBuilder status(String status) {
            friendRequest.setStatus(status);
            return this;
        }

        public FriendRequest build() {
            return friendRequest;
        }
    }
}
