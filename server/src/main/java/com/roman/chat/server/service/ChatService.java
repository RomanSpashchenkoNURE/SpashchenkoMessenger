package com.roman.chat.server.service;

import com.roman.chat.server.domain.entity.Chat;
import com.roman.chat.server.domain.entity.ChatMessage;
import com.roman.chat.server.domain.entity.ChatUser;
import com.roman.chat.server.domain.entity.UserInChat;
import com.roman.chat.server.domain.event.UserRegisteredEvent;
import com.roman.chat.server.repository.ChatMessageRepository;
import com.roman.chat.server.repository.ChatRepository;
import com.roman.chat.server.repository.ChatUserRepository;
import com.roman.chat.server.repository.UserInChatRepository;
import jakarta.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final UserInChatRepository userInChatRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final EntityManager entityManager;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, ChatUser> connectedUsers = new ConcurrentHashMap<>();

    public ChatUser findUserById(UUID id) {
        return chatUserRepository.findById(id).orElseThrow();
    }

    public ChatUser findUserByName(String userName) {
        return chatUserRepository.findByUserName(userName).orElse(null);
    }

    public ChatUser createUser(ChatUser chatUser) {
        chatUser = chatUserRepository.save(chatUser);
        entityManager.detach(chatUser);
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(chatUser));
        return chatUser;
    }

    public List<ChatUser> listAllUsers() {
        return new ArrayList<>(chatUserRepository.findAll()
            .stream()
            .peek(entityManager::detach)
            .toList());
    }

    public Chat createNewChat(UUID creatorId, String chatTitle, UUID interlocutorId) {
        var chatList = chatRepository.findAllByUsers(creatorId, interlocutorId);
        if (!chatList.isEmpty()) {
            return chatList.getFirst();
        }

        var chat = createChat(creatorId, chatTitle);
        var chatId = chat.getId();
        createUserInChat(chatId, creatorId);
        createUserInChat(chatId, interlocutorId);
        return chat;
    }

    public List<Chat> findAllUserChats(UUID userId) {
        var chatList = userInChatRepository.findAllByUserIdOrderByStartedAtDesc(userId)
            .stream()
            .flatMap(userInChat -> chatRepository.findById(userInChat.getChatId()).stream())
            .peek(entityManager::detach)
            .collect(Collectors.toList());
        return chatList;
    }

    public Chat findUserChat(UUID rUserId, UUID lUserId) {
        var chatList = chatRepository.findAllByUsers(rUserId, lUserId);
        return chatList.isEmpty() ? null : chatList.getFirst();
    }

    public ChatMessage createChatMessage(UUID userId, UUID chatId, String content) {
        var chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        chatMessage.setUserId(userId);
        chatMessage.setContent(content);
        chatMessage.setCreateAt(ZonedDateTime.now());
        chatMessage = chatMessageRepository.save(chatMessage);

        sendMessageNotification(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findAllChatMessages(UUID userId, UUID chatId) {
        return (userInChatRepository.existsByChatIdAndUserId(chatId, userId)) ?
            chatMessageRepository.findAllByChatIdOrderByCreateAt(chatId) : Collections.emptyList();
    }

    public List<ChatMessage> findAllChatMessages(UUID chatId) {
        return chatMessageRepository.findAllByChatIdOrderByCreateAt(chatId);
    }

    public void connectUser(UUID userId, String sessionId) {
        var chatUser = findUserById(userId);
        entityManager.detach(chatUser);
        connectedUsers.put(sessionId, chatUser);
    }

    @EventListener
    public void handleWebSocketDisconnectEventListener(SessionDisconnectEvent event) {
        var sessionId = event.getSessionId();
        connectedUsers.remove(sessionId);
    }

//    @EventListener
//    public void handleSessionSubscribeEventListener(SessionSubscribeEvent event) {
//        System.out.println("SessionSubscribeEvent connected: " + event);
//    }

//    @EventListener
//    public void handleSessionConnectedEventListener(SessionConnectedEvent event) {
//        System.out.println("Session connected: " + event);
//    }

    private Chat createChat(UUID creatorId, String chatTitle) {
        var chat = new Chat();
        chat.setTitle(chatTitle);
        chat.setOwnerUserId(creatorId);
        chat.setStartedAt(ZonedDateTime.now());
        return chatRepository.save(chat);
    }

    private UserInChat createUserInChat(UUID chatId, UUID userId) {
        var userInChat = new UserInChat();
        userInChat.setChatId(chatId);
        userInChat.setUserId(userId);
        userInChat.setStartedAt(ZonedDateTime.now());
        return userInChatRepository.save(userInChat);
    }

    private void sendMessageNotification(ChatMessage chatMessage) {
        var chatId = chatMessage.getChatId();
        var chatUsers = userInChatRepository.findAllByChatIdOrderByStartedAtDesc(chatId);
        chatUsers.forEach(userInChat -> {
            var userId = userInChat.getUserId();
            if (!chatMessage.getUserId().equals(userId)) {
                messagingTemplate.convertAndSend("/topic/chat/" + userId, chatMessage);
            }
        });
    }
}
