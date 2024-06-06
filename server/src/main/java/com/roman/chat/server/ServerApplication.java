package com.roman.chat.server;

import javafx.application.Application;
import net.rgielen.fxweaver.spring.boot.autoconfigure.FxWeaverAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
@Import(FxWeaverAutoConfiguration.class)
public class ServerApplication {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
