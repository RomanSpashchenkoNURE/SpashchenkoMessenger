package com.roman.chat.client.ui.controller;

import com.roman.chat.client.domain.event.MessageReceivedEvent;
import com.roman.chat.client.domain.model.ChatMessageDto;
import com.roman.chat.client.domain.model.ChatUserDto;
import com.roman.chat.client.service.ClientService;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
@RequiredArgsConstructor
@FxmlView("chat-app-ui-controller.fxml")
public class ChatAppUiController implements Initializable {
    private final ClientService clientService;

    @FXML
    public TextField hostNameTextField;
    @FXML
    public TextField portTextField;
    @FXML
    public TextField userNameTextField;
    @FXML
    public Button connectButton;
    @FXML
    public Button disconnectButton;
    @FXML
    public ListView<ChatUserDto> userListView;
    @FXML
    public ListView<ChatMessageDto> userChatListView;
    @FXML
    public TextArea messageTextArea;
    @FXML
    public Button sendButton;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        applyCustomizations();
    }

    @EventListener
    public void handleMessageReceivedEvent(MessageReceivedEvent event) {
        Platform.runLater(() -> {
            userChatListView.getItems().add(event.getMessageDto());
            scrollToLastItem(userChatListView);
            userChatListView.refresh();
        });
    }

    public void connectButtonActionHandler(ActionEvent actionEvent) {
        String userName = userNameTextField.getText();
        String hostName = hostNameTextField.getText();
        String port = portTextField.getText();
        var connected = clientService.connectToServer(userName, hostName, port);
        if (connected) {
            loadDataForLoggedInUser();
            changeControllerStatesOnConnect();
        }
    }

    public void disconnectButtonActionHandler(ActionEvent actionEvent) {
        if (clientService.disconnectFromServer()) {
            changeControllerStatesOnDisconnect();
            cleanUp();
        }
    }

    public void sendButtonActionHandler(ActionEvent actionEvent) {
        clientService.sendMessage(messageTextArea.getText());
        messageTextArea.setText(EMPTY);
        scrollToLastItem(userChatListView);
        userChatListView.refresh();
    }

    private void changeControllerStatesOnConnect() {
        connectButton.setDisable(true);
        disconnectButton.setDisable(false);
        hostNameTextField.setDisable(true);
        portTextField.setDisable(true);
        userNameTextField.setDisable(true);
        messageTextArea.setDisable(false);
        sendButton.setDisable(false);
    }

    private void changeControllerStatesOnDisconnect() {
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
        hostNameTextField.setDisable(false);
        portTextField.setDisable(false);
        userNameTextField.setDisable(false);
        messageTextArea.setDisable(true);
        sendButton.setDisable(true);
    }

    private void loadDataForLoggedInUser() {
        loadUserList();
        loadChatContent();
    }

    private void loadUserList() {
        var allUsers = clientService.getAllUsers();
        userListView.setItems(allUsers);

        if (!allUsers.isEmpty()) {
            ChatUserDto selectedUser = allUsers.getFirst();
            userListView.getSelectionModel().select(selectedUser);
            clientService.setSelectedUser(selectedUser);
        }
    }

    private void loadChatContent() {
        var currentChatMessages = clientService.getCurrentChatMessages();
        userChatListView.setItems(currentChatMessages);
    }

    private void applyCustomizations() {
        applyCustomizationsToUserListView();
        applyCustomizationsToUserChatListView();
    }

    private void applyCustomizationsToUserListView() {
        userListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ChatUserDto> call(ListView<ChatUserDto> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ChatUserDto item, boolean empty) {
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
            public ListCell<ChatMessageDto> call(ListView<ChatMessageDto> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ChatMessageDto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle(EMPTY);
                        } else {
                            setText(item.getCreateAt().format(formatter) + "\n" + item.getContent());
                            var currentUserId = clientService.getCurrentUser().getId();
                            if (currentUserId.equals(item.getUserId())) {
                                setStyle("-fx-alignment: CENTER-RIGHT;");
                            } else {
                                setStyle("-fx-alignment: CENTER-LEFT;");
                            }
                        }
                    }
                };
            }
        });
    }

    private void onUserSelected(ChatUserDto selectedUser) {
        if (selectedUser != null) {
            loadChatForSelectedUser(selectedUser);
        }
    }

    private void loadChatForSelectedUser(ChatUserDto selectedUser) {
        clientService.selectedUserChanged(selectedUser);
        loadChatContent();

    }

    private void cleanUp() {
        userListView.setItems(FXCollections.emptyObservableList());
        userChatListView.setItems(FXCollections.emptyObservableList());
        messageTextArea.setText(EMPTY);
    }

    private <T> void scrollToLastItem(ListView<T> listView) {
        int lastIndex = listView.getItems().size() - 1;
        if (lastIndex >= 0) {
            listView.scrollTo(lastIndex);
        }
    }
}
