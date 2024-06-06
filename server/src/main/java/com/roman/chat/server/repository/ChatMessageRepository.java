package com.roman.chat.server.repository;

import com.roman.chat.server.domain.entity.ChatMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface ChatMessageRepository extends JpaRepositoryImplementation<ChatMessage, UUID> {
    List<ChatMessage> findAllByChatIdOrderByCreateAt(UUID chatId);
}
