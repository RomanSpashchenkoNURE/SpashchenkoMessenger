package com.roman.chat.client;

import com.roman.chat.client.domain.event.MainStageReadyEvent;
import com.roman.chat.client.ui.controller.ChatAppUiController;
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
        Parent parent = fxWeaver.loadView(ChatAppUiController.class);
        Scene scene = new Scene(parent, 1280, 1024);
        stage.setScene(scene);
        stage.setTitle("Chat application Client");
        stage.show();
    }
}
