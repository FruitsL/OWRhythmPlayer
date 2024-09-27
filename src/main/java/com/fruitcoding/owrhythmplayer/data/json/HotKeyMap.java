package com.fruitcoding.owrhythmplayer.data.json;

import lombok.Getter;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 단축키 저장 (Key: KeyCode, Value: 오버워치 버튼 영문명)
 */
public class HotKeyMap extends JSONMap<Integer, String> {
    private static HotKeyMap instance;
    @Getter
    private Map<String, Integer> reverseMap;

    /**
     * 단축키가 저장된 파일 경로 가져오기
     * @return 단축키가 저장된 파일의 경로
     */
    @Override
    String getFilePath() {
        return STR."\{System.getProperty("user.dir")}/data/save/hotkey.json";
    }

    public static synchronized HotKeyMap getInstance() throws IOException {
        if(instance == null)
            instance = new HotKeyMap();
        return instance;
    }

    /**
     * json에서 가져온 데이터를 Map으로 변환
     * @throws IOException json 파일이 없을 시
     */
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
        refreshReverseMap();
    }

    public void refreshReverseMap() {
        reverseMap = this.map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue, Map.Entry::getKey
                ));
    }

    /**
     * json 파일에 없을 시 해당 데이터로 단축키 설정 (초기 단축키 설정)
     * @return 초기 단축키 설정
     */
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
        map.put(KeyEvent.VK_F11, "PLAY/STOP");
        map.put(KeyEvent.VK_F12, "PAUSE");
        map.put(KeyEvent.VK_1, "WEAPON1");
        map.put(KeyEvent.VK_2, "WEAPON2");
        return map;
    }

    @Override
    public Map<Integer, String> getMap() {
        return super.getMap();
    }

    public void changeKeyCode(String button, int keyCode) {
        map.remove(reverseMap.get(button));
        reverseMap.put(button, keyCode);
        map.put(keyCode, button);
    }
}
