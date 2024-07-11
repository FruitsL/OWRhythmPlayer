package com.fruitcoding.owrhythmplayer;

import com.fruitcoding.owrhythmplayer.util.GlobalKeyMouseListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.error;
import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, NativeHookException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        fxmlLoader.getController();
        stage.setTitle("Overwatch Rhythm Player");
        stage.setScene(scene);
        stage.show();

        new GlobalKeyMouseListener();

        new Panel().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                info(STR."KeyListener: \{e}");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                info(STR."keyPressed: \{e}");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                info(STR."keyReleased: \{e}");
            }
        });
    }

    @Override
    public void stop() {
        try {
            GlobalScreen.unregisterNativeHook();
            info("GlobalHooker Closed");
        } catch (NativeHookException e) {
            error("GlobalHooker Closed Error");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}