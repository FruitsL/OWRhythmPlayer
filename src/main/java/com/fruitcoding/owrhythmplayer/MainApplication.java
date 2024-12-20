package com.fruitcoding.owrhythmplayer;

import com.fruitcoding.owrhythmplayer.controller.MainController;
import com.fruitcoding.owrhythmplayer.data.json.HotKeyMap;
import com.fruitcoding.owrhythmplayer.util.LoggerUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.IOException;
import java.util.Map;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.*;

public class MainApplication extends Application {
    MainController mainController;
    LoggerUtil loggerUtil;

    public static void main(String[] args) {
        info("start");
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, NativeHookException {
        loggerUtil = new LoggerUtil();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 280);
        mainController = fxmlLoader.getController();
        stage.setTitle("Overwatch Rhythm Player");
        stage.setScene(scene);
        stage.show();

        scene.setOnDragOver(dragEvent -> {
            // 드래그 중인 데이터 수락 설정
            dragEvent.acceptTransferModes(TransferMode.ANY);
            dragEvent.consume();
        });
    }

    @Override
    public void stop() {
        try {
            Map<String, String> mainMap = mainController.getMainMap().getMap();
            mainMap.put("speakerDelayTextField1", mainController.getSpeakerDelayTextField1().getText());
            mainMap.put("speakerDelayTextField2", mainController.getSpeakerDelayTextField2().getText());
            mainMap.put("speakerDelayTextField3", mainController.getSpeakerDelayTextField3().getText());
            mainMap.put("speakerSplitMenuButton1", mainController.getSpeakerSplitMenuButton1().getText());
            mainMap.put("speakerSplitMenuButton2", mainController.getSpeakerSplitMenuButton2().getText());
            mainMap.put("titleCheckBox", String.valueOf(mainController.getTitleCheckBox().isSelected()));

            mainController.getMainMap().mapToJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            if(GlobalScreen.isNativeHookRegistered())
                GlobalScreen.unregisterNativeHook();
            info("GlobalHooker Closed");
        } catch (NativeHookException e) {
            error("GlobalHooker Closed Error");
        }
    }
}