package com.roman.chat.server;

import com.roman.chat.server.domain.event.MainStageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext springApplicationContext;

    @Override
    public void init() {
        springApplicationContext = SpringApplication.run(ServerApplication.class,
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
