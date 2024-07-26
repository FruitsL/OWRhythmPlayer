package com.fruitcoding.owrhythmplayer.controller;

import com.fruitcoding.owrhythmplayer.MainApplication;
import com.fruitcoding.owrhythmplayer.audio.AudioDevice;
import com.fruitcoding.owrhythmplayer.audio.AudioFileConverter;
import com.fruitcoding.owrhythmplayer.audio.AudioPlayer;
import com.fruitcoding.owrhythmplayer.controller.component.MapSplitMenuButton;
import com.fruitcoding.owrhythmplayer.controller.component.NumericTextField;
import com.fruitcoding.owrhythmplayer.controller.component.TooltipSlider;
import com.fruitcoding.owrhythmplayer.util.LoggerUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.VBox;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

public class MainController {
    @FXML
    private MapSplitMenuButton musicSplitMenuButton;
    @FXML
    private MapSplitMenuButton speakerSplitMenuButton1;
    @FXML
    private MapSplitMenuButton speakerSplitMenuButton2;
    @FXML
    private NumericTextField speakerDelayTextField1;
    @FXML
    private NumericTextField speakerDelayTextField2;
    @FXML
    private TooltipSlider speakerSlider1;
    @FXML
    private TooltipSlider speakerSlider2;

    AudioPlayer player1;
    AudioPlayer player2;

    @FXML
    public void initialize() {
        speakerDelayTextField1.setText("0");
        speakerDelayTextField2.setText("0");

        speakerSlider1.setValue(100.0);
        speakerSlider2.setValue(100.0);

        musicSplitMenuButton.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                newScene.setOnDragEntered(this::handleDragDropped);
            }
        });

        speakerSplitMenuButton1.setList(
                AudioDevice.getInstance().getSourceMixerInfos().stream()
                        .map(Mixer.Info::getName)
                        .collect(Collectors.toList())
        );
        speakerSplitMenuButton2.setList(
                AudioDevice.getInstance().getSourceMixerInfos().stream()
                        .map(Mixer.Info::getName)
                        .collect(Collectors.toList())
        );
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            musicSplitMenuButton.setMap(db.getFiles().stream()
                    .filter(File::isFile)
                    .collect(Collectors.toMap(File::getName, file -> file, (existing, replacement) -> existing)));
        }
    }

    @FXML
    private void play() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        File wavFile = AudioFileConverter.getInstance().getWavFile();

        player1 = new AudioPlayer((Mixer.Info) speakerSplitMenuButton1.getMap().get(speakerSplitMenuButton1.getIndex()), wavFile);
        player2 = new AudioPlayer((Mixer.Info) speakerSplitMenuButton2.getMap().get(speakerSplitMenuButton2.getIndex()), wavFile);
        player1.play(Long.parseLong(speakerDelayTextField1.getText()), (float)speakerSlider1.getValue()); // 중복 실행되는지 확인 필요
        player2.play(Long.parseLong(speakerDelayTextField2.getText()), (float)speakerSlider2.getValue());
    }

    @FXML
    private void stop() {
        player1.stop();
        player2.stop();
    }
}