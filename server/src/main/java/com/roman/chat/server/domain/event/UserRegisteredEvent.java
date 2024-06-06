package com.roman.chat.server.domain.event;

import com.roman.chat.server.domain.entity.ChatUser;
import org.springframework.context.ApplicationEvent;

public class UserRegisteredEvent extends ApplicationEvent {

    public UserRegisteredEvent(ChatUser chatUser) {
        super(chatUser);
    }

    public ChatUser getChatUser() {
        return (ChatUser) source;
    }
}
