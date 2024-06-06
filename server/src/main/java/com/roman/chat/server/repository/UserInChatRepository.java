package com.roman.chat.server.repository;

import com.roman.chat.server.domain.entity.UserInChat;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface UserInChatRepository extends JpaRepositoryImplementation<UserInChat, UUID> {
    List<UserInChat> findAllByUserIdOrderByStartedAtDesc(UUID userId);

    List<UserInChat> findAllByChatIdOrderByStartedAtDesc(UUID chatId);

    boolean existsByChatIdAndUserId(UUID chatId, UUID userId);
}
