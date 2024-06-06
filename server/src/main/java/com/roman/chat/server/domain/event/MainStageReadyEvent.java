package com.roman.chat.server.domain.event;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class MainStageReadyEvent extends ApplicationEvent {
    public MainStageReadyEvent(Stage stage) {
        super(stage);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
