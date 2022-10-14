/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
 * Copyright (C) 2021 Matteo Veroni Rebirth project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.rebirthproject.javafxappexample;

import it.rebirthproject.javafxappexample.eventemitters.EventEmitterRunnable;
import it.rebirthproject.javafxappexample.events.EventMessage;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private EventBus eventBus;
    private EventEmitterRunnable eventEmitterRunnable;
    private Thread eventEmitterThread;
    private final ListView<String> listViewOutput = new ListView<>();
    private final Button btnStopEmitter = new Button("Stop emitter");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        eventBus = new EventBusBuilder()
                .setNumberOfWorkers(1)
                .build();
        eventBus.register(this);
        eventEmitterRunnable = new EventEmitterRunnable(eventBus);
        eventEmitterThread = new Thread(eventEmitterRunnable);
        eventEmitterThread.start();
    }

    @Override
    public void start(Stage stage) {
        listViewOutput.setMaxWidth(500);

        stage.setTitle("Javafx App Test");
        stage.setWidth(800);
        stage.setHeight(600);

        btnStopEmitter.setOnAction(action -> {
            btnStopEmitter.setVisible(false);
            eventEmitterRunnable.stop();
        });

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(30);
        vBox.getChildren().add(listViewOutput);
        vBox.getChildren().add(btnStopEmitter);
        vBox.setSpacing(30);

        BorderPane rootPane = new BorderPane();
        rootPane.setCenter(vBox);

        stage.setScene(new Scene(rootPane));
        stage.show();
    }

    @Listen
    public void onEvent(EventMessage event) {
        Platform.runLater(() -> listViewOutput.getItems().add(event.getMessage()));
    }

    @Override
    public void stop() throws InterruptedException {
        eventEmitterRunnable.stop();
        eventEmitterThread.join();
        eventBus.shutdownBus();
    }
}
