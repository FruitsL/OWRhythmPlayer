package com.fruitcoding.owrhythmplayer.data;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainMap extends JSONMap<String, String> {

    @Override
    String getFilePath() {
        return STR."\{System.getProperty("user.dir")}/data/save/main.json";
    }

    public MainMap() throws IOException {
        try {
            jsonToMap();
            if(super.map.isEmpty())
                throw new IOException("file contents is empty!");
        } catch (IOException _) {
            creatJSONFile();
            super.map = initMap();
            mapToJSON();
        }
    }

    private Map<String, String> initMap() {
        Map<String, String> map = new HashMap<>();
        map.put("speakerDelayTextField1", "1000");
        map.put("speakerDelayTextField2", "3000");
        map.put("speakerSlider1", "50");
        map.put("speakerSlider2", "50");
        map.put("speakerSplitMenuButton1", "없음");
        map.put("speakerSplitMenuButton2", "없음");
        map.put("musicSplitMenuButton", "없음");
        return map;
    }

    @Override
    public Map<String, String> getMap() {
        return super.getMap();
    }
}
