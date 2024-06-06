package com.roman.chat.server.repository;

import com.roman.chat.server.domain.entity.Chat;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepositoryImplementation<Chat, UUID> {
    @Query(value = """
        SELECT *
        FROM CHAT_SERVER.CHAT C
        WHERE C.ID IN (
            SELECT L.CHAT_ID
            FROM CHAT_SERVER.USER_IN_CHAT L
                     JOIN CHAT_SERVER.USER_IN_CHAT R ON L.CHAT_ID = R.CHAT_ID
            WHERE R.USER_ID = :rUserId
              AND L.USER_ID = :lUserId)
          AND C.OWNER_USER_ID in (:rUserId, :lUserId)""", nativeQuery = true)
    List<Chat> findAllByUsers(@Param("rUserId") UUID rUserId, @Param("lUserId") UUID lUserId);
}
