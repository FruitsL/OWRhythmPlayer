package com.fruitcoding.owrhythmplayer.data.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingMap extends JSONMap<String, String> {
    private static SettingMap instance;

    @Override
    String getFilePath() {
        return System.getProperty("user.dir") + "/data/save/setting.json";
    }

    public static synchronized SettingMap getInstance() throws IOException {
        if(instance == null)
            instance = new SettingMap();
        return instance;
    }

    public SettingMap() throws IOException {
        try {
            jsonToMap(String.class, String.class);
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
        map.put("bpmCheckBox", "true");
        map.put("logCheckBox", "false");
        return map;
    }
}