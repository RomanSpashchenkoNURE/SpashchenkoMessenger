package com.roman.chat.client.integration.http;

import com.roman.chat.client.domain.model.ChatDto;
import com.roman.chat.client.domain.model.ChatMessageDto;
import com.roman.chat.client.domain.model.ChatUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "chatUserClient", url = "http://somehost:8080")
public interface ChatUserFeignClient {

    @GetMapping(path = "/api/chatUsers/{id}",
            consumes = {"application/json"})
    ChatUserDto findUserById(@PathVariable UUID id);

    @GetMapping(path = "/api/chatUsers/userName/{userName}",
            consumes = {"application/json"})
    ChatUserDto findUserByName(@PathVariable String userName);

    @PostMapping(path = "/api/chatUsers",
            consumes = {"application/json"}, produces = {"application/json"})
    ChatUserDto createUser(@RequestBody ChatUserDto chatUser);

    @GetMapping(path = "/api/chatUsers",
            consumes = {"application/json"})
    List<ChatUserDto> listAllUsers();

    @GetMapping(path = "/api/chatUsers/{userId}/chats",
            consumes = {"application/json"})
    List<ChatDto> findAllUserChats(@PathVariable UUID userId);

    @PostMapping(path = "/api/chatUsers/{userId}/chats",
            consumes = {"application/json"}, produces = {"application/json"})
    ChatDto createChat(
            @PathVariable UUID userId,
            @RequestParam(value = "chatTitle") String chatTitle,
            @RequestParam(value = "interlocutorId") UUID interlocutorId);

    @PostMapping(path = "/api/chatUsers/{userId}/chats/{chatId}/messages",
            consumes = {"application/json"}, produces = {"application/json"})
    ChatMessageDto createChatMessage(
            @PathVariable UUID userId,
            @PathVariable UUID chatId,
            @RequestBody String content);

    @GetMapping(path = "/api/chatUsers/{userId}/chats/{chatId}/messages",
            consumes = {"application/json"})
    List<ChatMessageDto> findAllUserChatMessages(@PathVariable UUID userId, @PathVariable UUID chatId);
}