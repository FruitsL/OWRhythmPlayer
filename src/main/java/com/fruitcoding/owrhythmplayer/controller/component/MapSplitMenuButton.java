package com.fruitcoding.owrhythmplayer.controller.component;

import com.fruitcoding.owrhythmplayer.audio.AudioDevice;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.error;
import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

@Getter
public class MapSplitMenuButton extends SplitMenuButton {
    private Map<?, ?> map = null;
    private int index = -1;

    public void setMap(Map<?, ?> map) {
        this.map = map;
        this.getItems().clear();

        map.keySet().forEach(k -> {
            MenuItem item = new MenuItem((String) k);
            this.getItems().add(item);
            setupMenuItem(item);
        });
    }

    /**
     * 메뉴 목록에 아이템 선택 시 인덱스 업데이트
     *
     * @param item 선택된 아이템
     */
    protected void setupMenuItem(MenuItem item) {
        item.setOnAction(_ -> {
            setIndex(getItems().indexOf(item));
        });
    }

    /**
     * index Setter (없는 인덱스가 들어오면 무시됨)
     *
     * @param index 설정할 인덱스 값
     */
    public void setIndex(int index) {
        try {
            this.setText(this.getItems().get(index).getText());
            this.index = index;
        } catch(Exception e) {
            error(e);
        }
    }

    public void setList(List<String> list) {
        list.stream().map(MenuItem::new)
                .peek(this::setupMenuItem)
                .forEach(this.getItems()::add);
        map = list.stream().collect(Collectors.toMap(s -> s, s -> s, (existing, replacement) -> existing));
    }
}
