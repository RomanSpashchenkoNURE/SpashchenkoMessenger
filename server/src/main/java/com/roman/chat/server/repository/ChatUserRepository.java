package com.roman.chat.server.repository;

import com.roman.chat.server.domain.entity.ChatUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface ChatUserRepository extends JpaRepositoryImplementation<ChatUser, UUID> {
    Optional<ChatUser> findByUserName(String username);
}
