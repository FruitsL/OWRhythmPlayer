package com.fruitcoding.owrhythmplayer.data;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

/**
 * 단축키 저장 (Key: KeyCode, Value: 오버워치 버튼 영문명)
 */
public class HotKeyMap extends JSONMap<Integer, String> {
    private static HotKeyMap instance;
    @Override
    String getFilePath() {
        return STR."\{System.getProperty("user.dir")}/data/save/hotkey.json";
    }

    public static synchronized HotKeyMap getInstance() throws IOException {
        if(instance == null)
            instance = new HotKeyMap();
        return instance;
    }

    private HotKeyMap() throws IOException {
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
        map.put(MouseEvent.BUTTON1 * -1, "PRIMARY_FIRE");
        map.put(MouseEvent.BUTTON3 * -1, "SECONDARY_FIRE");
        map.put(KeyEvent.VK_SHIFT, "ABILITY_1");
        map.put(KeyEvent.VK_E, "ABILITY_2");
        map.put(KeyEvent.VK_Q, "ULTIMATE");
        map.put(KeyEvent.VK_F, "INTERACT");
        map.put(KeyEvent.VK_SPACE, "JUMP");
        map.put(KeyEvent.VK_CONTROL, "CROUCH");
        map.put(KeyEvent.VK_V, "MELEE");
        map.put(KeyEvent.VK_R, "RELOAD");
        map.put(KeyEvent.VK_F11, "PLAY");
        map.put(KeyEvent.VK_F12, "STOP");
        return map;
    }

    @Override
    public Map<Integer, String> getMap() {
        return super.getMap();
    }
}
