package com.chatapp.chat_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false, length = 1000)
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public Message() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public static class MessageBuilder {
        private final Message message;

        public MessageBuilder() {
            this.message = new Message();
        }

        public MessageBuilder sender(User sender) {
            message.setSender(sender);
            return this;
        }

        public MessageBuilder receiver(User receiver) {
            message.setReceiver(receiver);
            return this;
        }

        public MessageBuilder content(String content) {
            message.setContent(content);
            return this;
        }

        public MessageBuilder timestamp(LocalDateTime timestamp) {
            message.setTimestamp(timestamp);
            return this;
        }

        public Message build() {
            return message;
        }
    }
}
