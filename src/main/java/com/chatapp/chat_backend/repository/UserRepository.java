package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	
    Optional<User> findByUsername(String username);
}
