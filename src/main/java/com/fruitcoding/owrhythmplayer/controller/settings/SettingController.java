package com.fruitcoding.owrhythmplayer.controller.settings;

import com.fruitcoding.owrhythmplayer.data.json.SettingMap;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.error;

public class SettingController {
    private SettingMap settingMap;
    @FXML
    private CheckBox bpmCheckBox;
    @FXML
    private CheckBox logCheckBox;

    public void setStage(Stage stage) {
        stage.setOnCloseRequest(this::onWindowClose);
    }

    @FXML
    public void initialize() throws IOException {
        settingMap = SettingMap.getInstance();
        bpmCheckBox.setSelected(Boolean.parseBoolean(settingMap.getMap().get("bpmCheckBox")));
        logCheckBox.setSelected(Boolean.parseBoolean(settingMap.getMap().get("logCheckBox")));
    }

    private void onWindowClose(WindowEvent event) {
        settingMap.getMap().put("bpmCheckBox", String.valueOf(bpmCheckBox.isSelected()));
        settingMap.getMap().put("logCheckBox", String.valueOf(logCheckBox.isSelected()));
        try {
            settingMap.mapToJSON();
        } catch (IOException e) {
            error("Failed to save setting.json.");
        }
    }
}