package com.fruitcoding.owrhythmplayer.controller;

import com.fruitcoding.owrhythmplayer.audio.AudioDevice;
import com.fruitcoding.owrhythmplayer.audio.AudioFileConverter;
import com.fruitcoding.owrhythmplayer.audio.AudioPlayer;
import com.fruitcoding.owrhythmplayer.controller.component.MapSplitMenuButton;
import com.fruitcoding.owrhythmplayer.controller.component.NumericTextField;
import com.fruitcoding.owrhythmplayer.controller.component.TooltipSlider;
import com.fruitcoding.owrhythmplayer.controller.settings.HotkeyController;
import com.fruitcoding.owrhythmplayer.controller.settings.SettingController;
import com.fruitcoding.owrhythmplayer.data.json.MainMap;
import com.fruitcoding.owrhythmplayer.data.json.SettingMap;
import com.fruitcoding.owrhythmplayer.file.osu.OsuFile;
import com.fruitcoding.owrhythmplayer.file.osu.OszFile;
import com.fruitcoding.owrhythmplayer.util.ClipBoard;
import com.fruitcoding.owrhythmplayer.util.GlobalKeyMouseListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.jnativehook.NativeHookException;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    private Button playButton;
    @FXML @Getter
    private CheckBox titleCheckBox;

    AudioPlayer player1;
    AudioPlayer player2;
    AudioDevice audioDevice;
    GlobalKeyMouseListener globalKeyMouseListener;
    ClipBoard clipBoard;

    public static long startTime = 0L;

    @Getter
    private MainMap mainMap;
    private SettingMap settingMap;
    private OsuFile osuFile;

    /**
     * MainController 생성 시 init
     * @throws IOException UI 오류 발생 시
     */
    @FXML
    public void initialize() throws IOException {
        mainMap = new MainMap();
        settingMap = SettingMap.getInstance();

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
            error("Failed to initialize clipboard.\n" + e);
            throw new RuntimeException(e);
        }

        if(System.getProperty("os.name").toLowerCase().contains("window")) { // Windows인 경우에만 사용
            try {
                globalKeyMouseListener = new GlobalKeyMouseListener(this);
            } catch (NativeHookException e) {
                error("GlobalKeyMouseListener not working.\n" + e);
                throw new RuntimeException(e);
            }
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
        osuFile = null;
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
                musicFileMap.put(file.getName(), file.getAbsolutePath());
            });

            // 음악 선택 전, Mixer 목록 새로고침 (해당 작업 미수행 시, 입출력 장치 변화가 있을 때 정상적으로 음악 재생 불가)
            audioDevice.setSourceMixerInfos();
            audioDevice.setTargetMixerInfos();

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
        if (startTime == 0) { // TODO: 곡 재생이 완료된 뒤 다시 재생을 하면 중지가 아닌 재생이 시작되는지 확인 필요
            // osu 파일 설정
            boolean isOsu = musicSplitMenuButton.getText().endsWith("osu");
            if (isOsu) {
                osuFile = OsuFile.builder()
                        .filePath((String) musicSplitMenuButton.getMap().get(musicSplitMenuButton.getText()))
                        .build();
                AudioFileConverter.getInstance().convertToWAV(Paths.get(osuFile.getFile().getAbsolutePath()).getParent() + "/" + osuFile.getAudioFileName());

                if (Boolean.parseBoolean(settingMap.getMap().get("bpmCheckBox")))
                    osuFile.getOsuMapInfo().inputBPM();
            } else {
                AudioFileConverter.getInstance().convertToWAV((String) musicSplitMenuButton.getMap().get(musicSplitMenuButton.getText()));
            }

            File wavFile = AudioFileConverter.getInstance().getWavFile();
            if (titleCheckBox.isSelected()) {
                clipBoard.copyToClipBoard(musicSplitMenuButton.getText().substring(0, musicSplitMenuButton.getText().lastIndexOf('.')));
                clipBoard.paste();
            }

            if (isOsu && Boolean.parseBoolean(settingMap.getMap().get("bpmCheckBox")))
                osuFile.getOsuMapInfo().playBPM(Long.parseLong(speakerDelayTextField3.getText()));
            startTime = System.nanoTime();
            if (isOsu)
                osuFile.getOsuMapInfo().playNote(Long.parseLong(speakerDelayTextField3.getText()));

            // TODO: 채보, 스피커1, 스피커2의 재생 대기가 이전 것 이후에 실행되고 있음 ex. 스피커1이 1000ms, 스피커2가 1000ms로 지정되어있으면 스피커2는 스피커1이 나오고 1초 뒤에 재생됨
            player1 = playerInit(wavFile, speakerSplitMenuButton1.getIndex(), Long.parseLong(speakerDelayTextField1.getText()) * 1_000_000, (float) speakerSlider1.getValue());
            player2 = playerInit(wavFile, speakerSplitMenuButton2.getIndex(), Long.parseLong(speakerDelayTextField2.getText()) * 1_000_000, (float) speakerSlider2.getValue());
            info("Start Time: " + startTime);
            Platform.runLater(() -> playButton.setText("중지")); // 다른 스레드에서도 동작시킬 수 있음
        } else if(startTime > 0) {
            stop();
            Platform.runLater(() -> playButton.setText("재생")); // 다른 스레드에서도 동작시킬 수 있음
        }else if(player1.isPaused() || player2.isPaused()) {
            playing(1000, 1000);
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
                warn("Not found osu file. " + e.toString());
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
        if(player1 != null)
            player1.stop();
        if(player2 != null)
            player2.stop();
        if(osuFile != null)
            osuFile.getOsuMapInfo().stopNote();
        startTime = 0L;
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

    /**
     * 메뉴의 아이템 클릭 시 해당 아이템의 id에 맞는 웹사이트 호출
     * @param event 메서드 호출한 MenuItem
     */
    @FXML
    public void openURI(ActionEvent event) {
        try {
            switch(((MenuItem)event.getSource()).getId()) {
                case "userManual":
                    Desktop.getDesktop().browse(new URI("https://github.com/FruitsL/OWRhythmPlayer/releases"));
                    break;
                case "sourceCode":
                    Desktop.getDesktop().browse(new URI("https://github.com/FruitsL/OWRhythmPlayer"));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            error(e);
        }
    }

    @FXML
    private void openHotkeySetting() throws IOException {
        // FXML 파일 로드
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/fruitcoding/owrhythmplayer/settings/hotkey-view.fxml"));
        Parent root = fxmlLoader.load();

        HotkeyController hotkeyController = fxmlLoader.getController();
        hotkeyController.setGlobalKeyMouseListener(globalKeyMouseListener);

        // 새로운 Stage 생성
        Stage newWindow = new Stage();
        newWindow.setTitle("단축키 설정");

        // 새로운 창을 모달 창으로 설정
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setResizable(false);

        // FXML 파일로부터 로드한 Scene 설정
        Scene scene = new Scene(root);
        newWindow.setScene(scene);

        hotkeyController.setStage(newWindow);

        // 모달 창 띄우기
        newWindow.showAndWait();
    }

    @FXML
    private void openProgramSetting() throws IOException {
        // FXML 파일 로드
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/fruitcoding/owrhythmplayer/settings/setting-view.fxml"));
        Parent root = fxmlLoader.load();

        // 새로운 Stage 생성
        Stage newWindow = new Stage();
        newWindow.setTitle("프로그램 설정");

        // 새로운 창을 모달 창으로 설정
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setResizable(false);

        // FXML 파일로부터 로드한 Scene 설정
        Scene scene = new Scene(root);
        newWindow.setScene(scene);

        SettingController settingController = fxmlLoader.getController();
        settingController.setStage(newWindow);

        // 모달 창 띄우기
        newWindow.showAndWait();
    }

    @FXML
    private void openMacroSetting() {

    }
}