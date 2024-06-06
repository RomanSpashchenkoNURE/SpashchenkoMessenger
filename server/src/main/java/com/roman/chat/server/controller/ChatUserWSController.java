package com.roman.chat.server.controller;

import com.roman.chat.server.service.ChatService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatUserWSController {
    private final ChatService chatService;

    @MessageMapping("/connect/{userId}")
    public void connect(@DestinationVariable UUID userId, @Header("simpSessionId") String sessionId) {
        chatService.connectUser(userId, sessionId);
    }
}
