package com.fruitcoding.owrhythmplayer.controller.component;

import com.fruitcoding.owrhythmplayer.audio.AudioDevice;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import lombok.Getter;

@Getter
public class SpeakerSplitMenuButton extends SplitMenuButton {
    private int index = -1;

    public SpeakerSplitMenuButton() {
        super();
        AudioDevice.getInstance().getTargetMixerInfos().stream()
                .map(mixerInfo -> new MenuItem(mixerInfo.getName()))
                .peek(this::setupMenuItem)
                .forEach(this.getItems()::add);
    }

    public SpeakerSplitMenuButton(int index) {
        this();
        setIndex(index);
    }

    /**
     * 메뉴 목록에 아이템 선택 시 인덱스 업데이트
     *
     * @param item 선택된 아이템
     */
    private void setupMenuItem(MenuItem item) {
        item.setOnAction(actionEvent -> {
            setIndex(getItems().indexOf(item));
        });
    }

    /**
     * index Setter (없는 인덱스가 들어오면 무시됨)
     *
     * @param index 설정할 인덱스 값
     */
    private void setIndex(int index) {
        try {
            this.setText(this.getItems().get(index).getText());
            this.index = index;
        } catch(Exception _) {}
    }

}
