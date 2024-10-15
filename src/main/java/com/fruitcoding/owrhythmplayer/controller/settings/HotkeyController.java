package com.fruitcoding.owrhythmplayer.controller.settings;

import com.fruitcoding.owrhythmplayer.data.json.HotKeyMap;
import com.fruitcoding.owrhythmplayer.util.GlobalKeyMouseListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Map;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.error;
import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;


public class HotkeyController {
    @FXML
    private Label explainLabel;
    @FXML
    private GridPane gridPane;

    private GlobalKeyMouseListener listener;

    @FXML
    public void initialize() throws IOException {
        Map<String, Integer> reverseHotkeyMap = HotKeyMap.getInstance().getReverseMap();
        for (Node node : gridPane.getChildren()) {
            Integer columnIndex = GridPane.getColumnIndex(node); // 자식 노드의 열(column) 인덱스가 0인지 확인

            if (columnIndex != null && columnIndex == 1) { // 첫 번째 열
                if (node instanceof Button button) { // 해당 노드가 Button일 경우 텍스트 설정
                    info("Button : " + button.getId());
                    button.setText(KeyEvent.getKeyText(reverseHotkeyMap.get(button.getId())));
                }
            }
        }
    }

    public void setStage(Stage stage) {
        stage.setOnCloseRequest(event -> {
            try {
                onWindowClose(event);
            } catch (IOException e) {
                error(e);
                throw new RuntimeException(e);
            }
        });
    }

    public void setGlobalKeyMouseListener(GlobalKeyMouseListener listener) {
        this.listener = listener;
    }

    @FXML
    private void changeHotkey(ActionEvent event) {
        Button btn = (Button)event.getSource();
        btn.setStyle("-fx-font-weight: bold;"); // 버튼 텍스트에 bold 효과 적용
        explainLabel.setText("변경할 키를 입력해주세요. (ESC: 취소)");

        listener.setButton(btn);
        listener.setHotkeyController(this);
    }

    public void setKey(Button button, int keyCode) {
        try {
            HotKeyMap.getInstance().changeKeyCode(button.getId(), keyCode);
            Platform.runLater(() -> button.setText(KeyEvent.getKeyText(keyCode)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        button.setStyle(null);
        Platform.runLater(() -> explainLabel.setText("버튼을 눌러 입력 키를 변경하세요.")); // 다른 스레드에서도 동작시킬 수 있음
    }

    private void onWindowClose(WindowEvent event) throws IOException {
        HotKeyMap hotKeyMap = HotKeyMap.getInstance();
        hotKeyMap.mapToJSON();
        hotKeyMap.refreshReverseMap();
    }
}