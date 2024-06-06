package com.roman.chat.client;

import javafx.application.Application;
import net.rgielen.fxweaver.spring.boot.autoconfigure.FxWeaverAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({FxWeaverAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
@EnableFeignClients
public class ClientApplication {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}
