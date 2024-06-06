package com.roman.chat.client.domain.event;

import com.roman.chat.client.domain.model.ChatMessageDto;
import org.springframework.context.ApplicationEvent;

public class MessageReceivedEvent extends ApplicationEvent {

    public MessageReceivedEvent(ChatMessageDto messageDto) {
        super(messageDto);
    }

    public ChatMessageDto getMessageDto() {
        return (ChatMessageDto) getSource();
    }
}
