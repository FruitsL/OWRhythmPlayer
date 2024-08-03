package com.fruitcoding.owrhythmplayer.controller;

import com.fruitcoding.owrhythmplayer.MainApplication;
import com.fruitcoding.owrhythmplayer.audio.AudioDevice;
import com.fruitcoding.owrhythmplayer.audio.AudioFileConverter;
import com.fruitcoding.owrhythmplayer.audio.AudioPlayer;
import com.fruitcoding.owrhythmplayer.controller.component.MapSplitMenuButton;
import com.fruitcoding.owrhythmplayer.controller.component.NumericTextField;
import com.fruitcoding.owrhythmplayer.controller.component.TooltipSlider;
import com.fruitcoding.owrhythmplayer.data.MainMap;
import com.fruitcoding.owrhythmplayer.file.osu.OszFile;
import com.fruitcoding.owrhythmplayer.util.LoggerUtil;
import it.sauronsoftware.jave.EncoderException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import lombok.Getter;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.error;
import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

public class MainController {
    @FXML
    private MapSplitMenuButton musicSplitMenuButton;
    @FXML @Getter
    private MapSplitMenuButton speakerSplitMenuButton1;
    @FXML @Getter
    private MapSplitMenuButton speakerSplitMenuButton2;
    @FXML @Getter
    private NumericTextField speakerDelayTextField1;
    @FXML @Getter
    private NumericTextField speakerDelayTextField2;
    @FXML
    private TooltipSlider speakerSlider1;
    @FXML
    private TooltipSlider speakerSlider2;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;

    AudioPlayer player1;
    AudioPlayer player2;
    AudioDevice audioDevice;

    @Getter
    private MainMap mainMap;
    private boolean isPlaying;

    @FXML
    public void initialize() throws IOException {
        mainMap = new MainMap();

        speakerDelayTextField1.setText(mainMap.getMap().get("speakerDelayTextField1"));
        speakerDelayTextField2.setText(mainMap.getMap().get("speakerDelayTextField2"));

        speakerSlider1.setValue(Double.parseDouble(mainMap.getMap().get("speakerSlider1")));
        speakerSlider1.addEventHandler(MouseEvent.MOUSE_RELEASED, _ -> mainMap.getMap().put("speakerSlider1", String.valueOf((int)speakerSlider1.getValue())));
        speakerSlider2.setValue(Double.parseDouble(mainMap.getMap().get("speakerSlider2")));
        speakerSlider1.addEventHandler(MouseEvent.MOUSE_RELEASED, _ -> mainMap.getMap().put("speakerSlider2", String.valueOf((int)speakerSlider2.getValue())));

        musicSplitMenuButton.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                newScene.setOnDragDropped(this::handleDragDropped);
            }
        });

        audioDevice = AudioDevice.getInstance();
        speakerSplitMenuButton1.setList(audioDevice.getSourceMixerInfosNameList());
        if(audioDevice.getSourceMixerInfosNameList().contains(mainMap.getMap().get("speakerSplitMenuButton1"))) {
            speakerSplitMenuButton1.setText(mainMap.getMap().get("speakerSplitMenuButton1"));
            speakerSplitMenuButton1.setIndex(audioDevice.getSourceMixerInfosNameList().indexOf(mainMap.getMap().get("speakerSplitMenuButton1")));
        }
        speakerSplitMenuButton2.setList(
                audioDevice.getSourceMixerInfos().stream()
                        .map(Mixer.Info::getName)
                        .collect(Collectors.toList())
        );
        if(audioDevice.getSourceMixerInfosNameList().contains(mainMap.getMap().get("speakerSplitMenuButton2"))) {
            speakerSplitMenuButton2.setText(mainMap.getMap().get("speakerSplitMenuButton2"));
            speakerSplitMenuButton2.setIndex(audioDevice.getSourceMixerInfosNameList().indexOf(mainMap.getMap().get("speakerSplitMenuButton2")));
        }
    }

    private void handleDragDropped(DragEvent event) {
        info("dragdrooped");
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            if(files.size() == 1) {
                File firstFile = files.getFirst();
                if(firstFile.isDirectory()) {// 폴더 1개만 있을 경우, 폴더 내 파일들을 추가
                    files = Arrays.asList(Objects.requireNonNull(firstFile.listFiles()));
                } else if (firstFile.getName().substring(firstFile.getName().lastIndexOf('.')).equals(".osz")) { // Osu 맵 압축 파일인 경우, 압축 해제 후 파일들 추가
                    try {
                        OszFile oszFile = new OszFile(firstFile);
                        files = oszFile.getFileMap().get("osu").values().stream().toList();
                    } catch (OszFile.NotAZipFileException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            Map<String, String> musicFileMap = new HashMap<>();
            files.forEach(file -> {
                String extension = file.getName().substring(file.getName().lastIndexOf('.'));
                switch (extension) {
                    case ".mp3":
                    case ".ogg":
                    case ".wav":
                    case ".osu":
                        musicFileMap.put(file.getName(), file.getAbsolutePath());
                        break;
                    default:
                        break;
                }
            });

            musicSplitMenuButton.setMap(musicFileMap);
            musicSplitMenuButton.setIndex(0);
            try {
                AudioFileConverter.getInstance().convertToWAV(musicFileMap.get(files.getFirst().getName()));
            } catch (EncoderException e) {
                error("wav convert error");
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void play() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if(isPlaying) {
            stop();
            isPlaying = false;
        } else if(musicSplitMenuButton.getMap() != null) {
            File wavFile = AudioFileConverter.getInstance().getWavFile();

            if(player1 != null)
                player1.stop();
            player1 = playing(wavFile, speakerSplitMenuButton1.getIndex(), Long.parseLong(speakerDelayTextField1.getText()), (float)speakerSlider1.getValue());
            if(player2 != null)
                player2.stop();
            player2 = playing(wavFile, speakerSplitMenuButton2.getIndex(), Long.parseLong(speakerDelayTextField2.getText()), (float)speakerSlider2.getValue());
            isPlaying = true;

            playButton.setText("중지");
        }
    }

    private AudioPlayer playing(File wavFile, int index, long delay, float volume) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        AudioPlayer newAudioPlayer;
        if (index >= 0) {
            newAudioPlayer = new AudioPlayer(audioDevice.getSourceMixerInfos().get(index), wavFile);
            newAudioPlayer.play(delay, volume);
            return newAudioPlayer;
        }
        return null;
    }

    private void stop() {
        if(player1 != null)
            player1.stop();
        if(player2 != null)
            player2.stop();

        playButton.setText("재생");
    }

    @FXML
    private void pause() {
        if(player1 != null)
            player1.pause();
        if(player2 != null)
            player2.pause();
        isPlaying = false;
    }
}