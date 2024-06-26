package com.fruitcoding.owrhythmplayer.controller;

import com.fruitcoding.owrhythmplayer.audio.AudioDevice;
import com.fruitcoding.owrhythmplayer.controller.component.NumericTextField;
import com.fruitcoding.owrhythmplayer.controller.component.SpeakerSplitMenuButton;
import com.fruitcoding.owrhythmplayer.util.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    private SpeakerSplitMenuButton speakerSplitMenuButton1;
    @FXML
    private SpeakerSplitMenuButton speakerSplitMenuButton2;
    @FXML
    private NumericTextField speakerDelayTextField2;

    @FXML
    public void initialize() {

    }
}