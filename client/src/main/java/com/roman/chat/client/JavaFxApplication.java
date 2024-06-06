package com.roman.chat.client;

import com.roman.chat.client.domain.event.MainStageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext springApplicationContext;

    @Override
    public void init() {
        springApplicationContext = SpringApplication.run(ClientApplication.class,
            getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        springApplicationContext.publishEvent(new MainStageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        springApplicationContext.close();
        Platform.exit();
    }

}
