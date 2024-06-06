package com.roman.chat.client.service;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ChatClientStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
    //TODO: handle disconnect
    }

}
