package com.roman.chat.server;

import com.roman.chat.server.domain.event.MainStageReadyEvent;
import com.roman.chat.server.ui.controller.ChatUiController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PrimaryStageInitializer implements ApplicationListener<MainStageReadyEvent> {
    private final FxWeaver fxWeaver;

    @Override
    public void onApplicationEvent(MainStageReadyEvent event) {
        Stage stage = event.getStage();
        Parent parent = fxWeaver.loadView(ChatUiController.class);
        Scene scene = new Scene(parent, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Chat application Server Console");
        stage.show();
    }
}
