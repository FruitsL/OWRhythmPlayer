package com.fruitcoding.owrhythmplayer.controller;

import com.fruitcoding.owrhythmplayer.MainApplication;
import com.fruitcoding.owrhythmplayer.audio.AudioDevice;
import com.fruitcoding.owrhythmplayer.audio.AudioFileConverter;
import com.fruitcoding.owrhythmplayer.audio.AudioPlayer;
import com.fruitcoding.owrhythmplayer.controller.component.MapSplitMenuButton;
import com.fruitcoding.owrhythmplayer.controller.component.NumericTextField;
import com.fruitcoding.owrhythmplayer.controller.component.TooltipSlider;
import com.fruitcoding.owrhythmplayer.data.MainMap;
import com.fruitcoding.owrhythmplayer.file.osu.OsuFile;
import com.fruitcoding.owrhythmplayer.file.osu.OszFile;
import com.fruitcoding.owrhythmplayer.map.osu.OsuMapInfo;
import com.fruitcoding.owrhythmplayer.util.ClipBoard;
import com.fruitcoding.owrhythmplayer.util.GlobalKeyMouseListener;
import com.fruitcoding.owrhythmplayer.util.LoggerUtil;
import it.sauronsoftware.jave.EncoderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.jnativehook.NativeHookException;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.*;

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
    @FXML @Getter
    private NumericTextField speakerDelayTextField3;
    @FXML
    private TooltipSlider speakerSlider1;
    @FXML
    private TooltipSlider speakerSlider2;
    @FXML
    private Button playButton; // TODO: 중지버튼 눌렀을 때 일부 기능 중지안되는 문제 해결 필요
    @FXML
    private Button pauseButton; // TODO: 일시정지 버튼 추가 필요
    @FXML
    private CheckBox titleCheckBox;
    @FXML
    private Checkbox bpmCheckBox;

    AudioPlayer player1;
    AudioPlayer player2;
    AudioDevice audioDevice;
    GlobalKeyMouseListener globalKeyMouseListener;
    ClipBoard clipBoard;

    public static long startTime = 0L;

    @Getter
    private MainMap mainMap;
    private OsuFile osuFile;

    /**
     * MainController 생성 시 init
     * @throws IOException UI 오류 발생 시
     */
    @FXML
    public void initialize() throws IOException {
        mainMap = new MainMap();

        speakerDelayTextField1.setText(mainMap.getMap().get("speakerDelayTextField1"));
        speakerDelayTextField2.setText(mainMap.getMap().get("speakerDelayTextField2"));
        speakerDelayTextField3.setText(mainMap.getMap().get("speakerDelayTextField3"));

        titleCheckBox.setSelected(Boolean.parseBoolean(mainMap.getMap().get("titleCheckBox")));

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

        try {
            clipBoard = ClipBoard.getInstance();
        } catch (AWTException e) {
            error(STR."Failed to initialize clipboard.\n\{e}}");
            throw new RuntimeException(e);
        }

        try {
            globalKeyMouseListener = new GlobalKeyMouseListener(this);
        } catch (NativeHookException e) {
            error(STR."GlobalKeyMouseListener not working.\n\{e}");
            throw new RuntimeException(e);
        }
    }

    /**
     * 파일을 UI로 드래그 앤 드랍 시 동작할 메서드
     * @param event 드래그 앤 드랍 시 생성되는 이벤트 (파일 목록 등)
     */
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        player1 = null;
        player2 = null;
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
                info(extension);
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

            info(musicFileMap);
            musicSplitMenuButton.setMap(musicFileMap);
            musicSplitMenuButton.setIndex(0);
        }
    }

    /**
     * "재생" 버튼 클릭 시 동작할 메서드
     * @throws UnsupportedAudioFileException 지원되지 않는 오디오
     * @throws LineUnavailableException 지원되지 않는 재생 장치
     * @throws IOException 음악 파일에 문제 발생
     * @throws AWTException UI에 문제 발생
     */
    @FXML
    public void onPlayButtonClick() throws UnsupportedAudioFileException, LineUnavailableException, IOException, AWTException {
        if(player1 == null || player2 == null) { // TODO: 곡 재생이 완료된 뒤 다시 재생을 하면 중지가 아닌 재생이 시작되는지 확인 필요
            // osu 파일 설정
            boolean isOsu = musicSplitMenuButton.getText().endsWith("osu");
            if(isOsu) {
                osuFile = OsuFile.builder()
                        .filePath((String) musicSplitMenuButton.getMap().get(musicSplitMenuButton.getText()))
                        .build();
                AudioFileConverter.getInstance().convertToWAV(STR."\{Paths.get(osuFile.getFile().getAbsolutePath()).getParent()}/\{osuFile.getAudioFileName()}");

                osuFile.getOsuMapInfo().inputBPM();
            } else {
                AudioFileConverter.getInstance().convertToWAV((String) musicSplitMenuButton.getMap().get(musicSplitMenuButton.getText()));
            }

            File wavFile = AudioFileConverter.getInstance().getWavFile();
            if(titleCheckBox.isSelected()) {
                clipBoard.copyToClipBoard(musicSplitMenuButton.getText().substring(0, musicSplitMenuButton.getText().lastIndexOf('.')));
                clipBoard.paste();
            }
            startTime = System.nanoTime();

            osuFile.getOsuMapInfo().playBPM(Long.parseLong(speakerDelayTextField3.getText()));
            osuFile.getOsuMapInfo().playNote(Long.parseLong(speakerDelayTextField3.getText()));
            player1 = playerInit(wavFile, speakerSplitMenuButton1.getIndex(), Long.parseLong(speakerDelayTextField1.getText()), (float)speakerSlider1.getValue());
            player2 = playerInit(wavFile, speakerSplitMenuButton2.getIndex(), Long.parseLong(speakerDelayTextField2.getText()), (float)speakerSlider2.getValue());
            info(STR."Start Time: \{startTime}");
            Platform.runLater(() -> playButton.setText("중지")); // 다른 스레드에서도 동작시킬 수 있음
        } else if(player1.isPaused() || player2.isPaused()) {
            playing(1000, 1000);
        } else if(player1.isPlaying() || player2.isPlaying()) {
            stop();
            Platform.runLater(() -> playButton.setText("재생")); // 다른 스레드에서도 동작시킬 수 있음
        }
    }

    /**
     * 음악 재생을 위한 플레이어 초기 설정 & 재생
     * @param wavFile 재생할 파일
     * @param index SplitMenuButton에서 선택한 Item의 인덱스
     * @param delay 시작 대기시간 (ms)
     * @param volume 음악 볼륨 (0.0 ~ 100.0)
     * @return 음악 파일이 포함된 오디오 플레이어 (없을 시 null)
     * @throws UnsupportedAudioFileException 지원하지 않는 재생 장치일 경우
     * @throws LineUnavailableException 사용할 수 없는 재생 장치일 경우
     * @throws IOException 음악 파일에 문제가 있을 경우
     */
    private AudioPlayer playerInit(File wavFile, int index, long delay, float volume) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        AudioPlayer newAudioPlayer;
        if (index >= 0) {
            newAudioPlayer = new AudioPlayer(audioDevice.getSourceMixerInfos().get(index), wavFile);
            newAudioPlayer.play(delay, volume);
            newAudioPlayer.getAudioClip().addLineListener(event -> { // 중지 시 재생 버튼 텍스트 변경
                if(event.getType() == LineEvent.Type.STOP) {
                    if(!(player1.isPlaying() || player2.isPlaying())) {
                        Platform.runLater(() -> {
                            playButton.setText("재생");
                        });
                    }
                }
            });
            return newAudioPlayer;
        } else {
            error("Failed Player init.");
            return null;
        }
    }

    public void playing(long d1, long d2) {
        boolean isOsu = musicSplitMenuButton.getText().endsWith("osu");
        if(isOsu) {
            try {
                osuFile = new OsuFile((String) musicSplitMenuButton.getMap().get(musicSplitMenuButton.getText()), new File(String.valueOf(musicSplitMenuButton.getMap().get(musicSplitMenuButton.getText()))));
            } catch (IOException e) {
                warn(STR."Not found osu file. \{e.toString()}");
                throw new RuntimeException(e);
            } catch (AWTException e) {
                error(e);
                throw new RuntimeException(e);
            }
        }
//        osuFile.getOsuMapInfo().playBPM(); // BPM 입력

        startTime = System.nanoTime();
        if(player1 != null)
            player1.play(d1, (float)speakerSlider1.getValue());
        if(player2 != null)
            player2.play(d2, (float)speakerSlider2.getValue());
        if(isOsu)
            osuFile.getOsuMapInfo().playNote(d1); // 노트 재생
    }

    /**
     * 재생 중인 음악을 중지
     */
    public void stop() {
        if(player1 != null) {
            player1.stop();
            player1 = null;
        }
        if(player2 != null) {
            player2.stop();
            player2 = null;
        }
        if(osuFile != null) {
            osuFile.getOsuMapInfo().stopNote();
            osuFile = null;
        }
    }

    /**
     * 재생 중인 음악을 일시정지
     */
    @FXML
    public void onPauseButtonClick() {
        if(musicSplitMenuButton.getText().endsWith("osu")) // osu 파일일 경우 일시정지 불가
            return; // TODO: 추후에 채보 파일이 있어도 일시정지가 가능하게 만들 예정
        if(player1.isPlaying() && player2.isPlaying()) {
            if (player1 != null)
                player1.pause();
            if (player2 != null)
                player2.pause();
        } else {
            playing(1_000, 1_000); // TODO: 현재는 1초로 고정되어있으나 추후 설정으로 시간이 변경 가능하도록 기능 추가 예정
        }
    }
}