package com.chatapp.chat_backend.model;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    // ----- Constructor -----
    public User() {}

    // ----- Getter ve Setter'lar -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ----- Manual Builder -----
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private final User user;

        public UserBuilder() {
            this.user = new User();
        }

        public UserBuilder username(String username) {
            user.setUsername(username);
            return this;
        }

        public UserBuilder password(String password) {
            user.setPassword(password);
            return this;
        }

        public UserBuilder email(String email) {
            user.setEmail(email);
            return this;
        }

        public User build() {
            return user;
        }
    }
}
