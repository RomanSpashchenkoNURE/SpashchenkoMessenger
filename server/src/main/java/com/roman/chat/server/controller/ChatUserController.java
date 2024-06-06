package com.roman.chat.server.controller;

import com.roman.chat.server.domain.entity.Chat;
import com.roman.chat.server.domain.entity.ChatMessage;
import com.roman.chat.server.domain.entity.ChatUser;
import com.roman.chat.server.service.ChatService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatUsers")
@RequiredArgsConstructor
public class ChatUserController {

    private final ChatService chatService;

    @GetMapping(path = "/{id}",
        produces = {"application/json"}
    )
    public ResponseEntity<ChatUser> findUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(chatService.findUserById(id));
    }

    @GetMapping(path = "/userName/{userName}",
        produces = {"application/json"})
    public ResponseEntity<ChatUser> findUserByName(@PathVariable String userName) {
        return ResponseEntity.ok(chatService.findUserByName(userName));
    }

    @PostMapping(path = "",
        consumes = {"application/json"},
        produces = {"application/json"})
    public ResponseEntity<ChatUser> createUser(@RequestBody ChatUser chatUser) {
        return ResponseEntity.ok(chatService.createUser(chatUser));
    }

    @GetMapping(path = "",
        produces = {"application/json"})
    public ResponseEntity<List<ChatUser>> listAllUsers() {
        return ResponseEntity.ok(chatService.listAllUsers());
    }

    @GetMapping(path = "/{userId}/chats",
        produces = {"application/json"})
    public ResponseEntity<List<Chat>> findAllUserChats(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatService.findAllUserChats(userId));
    }

    @PostMapping(path = "/{userId}/chats",
        consumes = {"application/json"},
        produces = {"application/json"})
    public ResponseEntity<Chat> createChat(
        @PathVariable UUID userId,
        @RequestParam(value = "chatTitle") String chatTitle,
        @RequestParam(value = "interlocutorId") UUID interlocutorId) {

        return ResponseEntity.ok(chatService.createNewChat(userId, chatTitle, interlocutorId));
    }

    @PostMapping(path = "/{userId}/chats/{chatId}/messages",
        consumes = {"application/json"},
        produces = {"application/json"})
    public ResponseEntity<ChatMessage> createChatMessage(
        @PathVariable UUID userId,
        @PathVariable UUID chatId,
        @RequestBody String content) {

        return ResponseEntity.ok(chatService.createChatMessage(userId, chatId, content));
    }

    @GetMapping(path = "/{userId}/chats/{chatId}/messages",
        produces = {"application/json"})
    public ResponseEntity<List<ChatMessage>> findAllUserChatMessages(@PathVariable UUID userId, @PathVariable UUID chatId) {
        return ResponseEntity.ok(chatService.findAllChatMessages(userId, chatId));
    }
}
