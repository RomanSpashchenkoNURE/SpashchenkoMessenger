package com.roman.chat.server.ui.controller;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.roman.chat.server.domain.entity.Chat;
import com.roman.chat.server.domain.entity.ChatMessage;
import com.roman.chat.server.domain.entity.ChatUser;
import com.roman.chat.server.domain.event.UserRegisteredEvent;
import com.roman.chat.server.service.ChatService;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FxmlView("chat-ui-controller.fxml")
public class ChatUiController implements Initializable {
    private final ChatService chatService;

    private ObservableList<ChatUser> allUsers;
    private ObservableList<Chat> selectUserAllChats;
    private ObservableList<ChatMessage> selectChatMessages;

    @FXML
    private ListView<ChatUser> userListView;

    @FXML
    private ListView<Chat> userChatListView;

    @FXML
    public ListView<ChatMessage> userChatMessagesListView;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @EventListener
    public void handleUserSelectedEvent(UserRegisteredEvent event) {
        Platform.runLater(() -> {
            ChatUser selectedUser = event.getChatUser();
            allUsers.add(selectedUser);
            userListView.refresh();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareModel();
        applyCustomizations();
    }

    private void prepareModel() {
        allUsers = FXCollections.observableList(chatService.listAllUsers());
        userListView.setItems(allUsers);
        ChatUser selectedUser = null;
        if (!allUsers.isEmpty()) {
            selectedUser = allUsers.getFirst();
            userListView.getSelectionModel().select(selectedUser);
        }
        selectUserAllChats = FXCollections.observableList(selectedUser != null ? chatService.findAllUserChats(selectedUser.getId()) : new ArrayList<>());
        userChatListView.setItems(selectUserAllChats);
        Chat selectedChat = null;
        if (!selectUserAllChats.isEmpty()) {
            selectedChat = selectUserAllChats.getFirst();
            userChatListView.getSelectionModel().select(selectedChat);
        }
        selectChatMessages = FXCollections.observableList(selectedChat != null ? chatService.findAllChatMessages(selectedUser.getId(),selectedChat.getId()) : new ArrayList<>());
        userChatMessagesListView.setItems(selectChatMessages);
    }

    private void applyCustomizations() {
        applyCustomizationsToUserListView();
        applyCustomizationsToUserChatListView();
        applyCustomizationsToUserChatMessagesListView();
    }

    private void applyCustomizationsToUserListView() {
        userListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ChatUser> call(ListView<ChatUser> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ChatUser item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getUserName());
                        }
                    }
                };
            }
        });

        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onUserSelected(newValue));
    }

    private void applyCustomizationsToUserChatListView() {
        userChatListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Chat> call(ListView<Chat> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Chat item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getTitle());
                        }
                    }
                };
            }
        });

        userChatListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChatSelected(newValue));
    }

    private void applyCustomizationsToUserChatMessagesListView() {
        userChatMessagesListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ChatMessage> call(ListView<ChatMessage> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ChatMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle(EMPTY);
                        } else {
                            var chatUser = allUsers
                                    .stream()
                                    .filter(user -> user.getId().equals(item.getUserId()))
                                    .findFirst()
                                    .orElse(null);

                            setText(item.getCreateAt().format(formatter) + " " + (chatUser != null ? chatUser.getUserName() : EMPTY) + "\n" + item.getContent() + "\n ");
                        }
                    }
                };
            }
        });
    }

    private void onUserSelected(ChatUser selectedUser) {
        if (selectedUser != null) {
            loadUserChatsForSelectedUser(selectedUser);
        }
    }

    private void loadUserChatsForSelectedUser(ChatUser selectedUser) {
        selectUserAllChats.clear();
        selectUserAllChats.addAll(chatService.findAllUserChats(selectedUser.getId()));
        userChatListView.refresh();
    }

    private void onChatSelected(Chat selectedChat) {
        if (selectedChat != null) {
            loadMessagesForSelectedChat(selectedChat);
        }
    }

    private void loadMessagesForSelectedChat(Chat selectedChat) {
        selectChatMessages.clear();
        selectChatMessages.addAll(chatService.findAllChatMessages(selectedChat.getId()));
        userChatMessagesListView.refresh();
    }
}
