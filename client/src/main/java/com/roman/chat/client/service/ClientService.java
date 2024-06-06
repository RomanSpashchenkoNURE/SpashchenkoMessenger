package com.roman.chat.client.service;

import com.roman.chat.client.domain.event.MessageReceivedEvent;
import com.roman.chat.client.domain.model.ChatDto;
import com.roman.chat.client.domain.model.ChatMessageDto;
import com.roman.chat.client.domain.model.ChatUserDto;
import com.roman.chat.client.integration.http.ChatUserFeignClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final FeignClientRequestInterceptor feignClientRequestInterceptor;
    private final ChatUserFeignClient chatUserFeignClient;
    private final WebSocketStompClient webSocketStompClient;
    private final StompSessionHandlerAdapter stompSessionHandlerAdapter;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Getter
    @Setter
    private ChatUserDto currentUser;

    @Getter
    @Setter
    private ChatUserDto selectedUser;

    @Setter
    private ObservableList<ChatUserDto> allUsers;

    @Getter
    @Setter
    private ChatDto currentChat;

    @Setter
    private ObservableList<ChatMessageDto> currentChatMessages;

    private StompSession currentStompSession;

    public boolean connectToServer(String userName, String ipAddress, String port) {
        try {
            setServerUrl(ipAddress, port);
            currentUser = chatUserFeignClient.findUserByName(userName);
            if (currentUser == null) {
                currentUser = chatUserFeignClient.createUser(new ChatUserDto().setUserName(userName));
            }
            wsConnect(ipAddress, port);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean disconnectFromServer() {
        setServerUrl(EMPTY, EMPTY);
        if (currentStompSession.isConnected()) {
            currentStompSession.disconnect();
        }
        currentUser = null;
        selectedUser = null;
        allUsers = null;
        currentChat = null;
        currentChatMessages = null;
        return true;
    }

    public ObservableList<ChatUserDto> getAllUsers() {
        if (allUsers == null) {
            allUsers = FXCollections.observableList(listAllUsers());
        }
        return allUsers;
    }

    public ObservableList<ChatMessageDto> getCurrentChatMessages() {
        if (selectedUser == null) {
            return FXCollections.emptyObservableList();
        }
        if (currentChat == null) {
            currentChat = chatUserFeignClient.createChat(
                    currentUser.getId(),
                    currentUser.getUserName() + " <---> " + selectedUser.getUserName(),
                    selectedUser.getId());
        }
        currentChatMessages = FXCollections.observableList(chatUserFeignClient.findAllUserChatMessages(currentUser.getId(), currentChat.getId()));
        return currentChatMessages;
    }

    public void selectedUserChanged(ChatUserDto selectedUser) {
        this.selectedUser = selectedUser;
        currentChat = null;
        currentChatMessages = null;
    }

    public void sendMessage(String message) {
        var chatMessage = chatUserFeignClient.createChatMessage(currentUser.getId(), currentChat.getId(), message);
        currentChatMessages.add(chatMessage);
    }

    private List<ChatUserDto> listAllUsers() {
        var currentUserId = currentUser.getId();
        return chatUserFeignClient.listAllUsers()
                .stream()
                .filter(user -> !currentUserId.equals(user.getId()))
                .collect(Collectors.toList());
    }

    private void setServerUrl(String ipAddress, String port) {
        feignClientRequestInterceptor.setServerUrl("http://" + ipAddress + ":" + port + "/");
    }

    private void wsConnect(String ipAddress, String port) throws InterruptedException, ExecutionException {
        String url = "ws://" + ipAddress + ":" + port + "/chat-ws";
        currentStompSession = webSocketStompClient.connectAsync(url, stompSessionHandlerAdapter).get();
        var currentUserId = currentUser.getId();
        currentStompSession.subscribe("/topic/chat/" + currentUserId, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                if (payload !=  null) {
                    handleMessageReceived((ChatMessageDto) payload);
                }
            }
        });
        currentStompSession.send("/app/connect/" + currentUserId, EMPTY);
    }

    private void handleMessageReceived(ChatMessageDto chatMessageDto) {
        if (currentChat != null
                && currentChat.getId().equals(chatMessageDto.getChatId())
                && !currentUser.getId().equals(chatMessageDto.getUserId())) {
//            currentChatMessages.add(chatMessageDto);
            applicationEventPublisher.publishEvent(new MessageReceivedEvent(chatMessageDto));
        }
    }

}