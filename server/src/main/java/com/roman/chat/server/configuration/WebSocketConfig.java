package com.roman.chat.server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //    @Autowired
    private TaskScheduler messageBrokerTaskScheduler;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler messageBrokerTaskScheduler) {
        this.messageBrokerTaskScheduler = messageBrokerTaskScheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-ws");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
            .setApplicationDestinationPrefixes("/app")
            .enableSimpleBroker("/topic", "/queue")
            .setHeartbeatValue(new long[] {10000, 10000})
            .setTaskScheduler(this.messageBrokerTaskScheduler);
    }

//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
//        System.out.println("messageConverters: " + messageConverters);
////        messageConverters.add(new MappingJackson2MessageConverter());
//        return true; // Adding our converter without replacing default ones
//    }
}