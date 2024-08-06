package com.fruitcoding.owrhythmplayer.data;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LineMap extends JSONMap<Integer, String> {
    private static LineMap instance;

    public static synchronized LineMap getInstance() throws IOException {
        if(instance == null)
            instance = new LineMap();
        return instance;
    }

    @Override
    String getFilePath() {
        return STR."\{System.getProperty("user.dir")}/data/save/line.json";
    }

    private LineMap() throws IOException {
        try {
            jsonToMap(Integer.class, String.class);
            if(super.map.isEmpty())
                throw new IOException("file contents is empty!");
        } catch (IOException _) {
            creatJSONFile();
            super.map = initMap();
            mapToJSON();
        }
    }

    private Map<Integer, String> initMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "ABILITY_1");
        map.put(1, "CROUCH");
        map.put(2, "JUMP");
        map.put(3, "MELEE");
        return map;
    }
}
