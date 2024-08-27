package com.fruitcoding.owrhythmplayer.map.base;

import com.fruitcoding.owrhythmplayer.data.HotKeyMap;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

import static com.fruitcoding.owrhythmplayer.controller.MainController.startTime;

abstract public class MapInfo {
    @Getter
    public Queue<NoteInfo> noteInfos = new LinkedList<NoteInfo>();
    @Getter
    public Queue<BPMInfo> bpmInfos = new LinkedList<BPMInfo>();

    Robot robot = null;
    @Setter @Getter
    private int initBPM = 0;

    public abstract void addNoteInfosByString(Object info);
    public abstract void addBPMInfosByString(Object info);

    public Map<String, Integer> hotkeyMap;
    Map<Integer, Integer> numToKeyMap;

    /**
     * 로봇 생성
     *
     * @throws AWTException
     */
    protected MapInfo() throws AWTException, IOException {
        robot = new Robot();
        hotkeyMap = HotKeyMap.getInstance().getMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue, Map.Entry::getKey
                ));
        numToKeyMap = Map.of( // 2 ^ k
                0, Math.abs(hotkeyMap.get("PRIMARY_FIRE")),
                1, Math.abs(hotkeyMap.get("SECONDARY_FIRE")),
                2, hotkeyMap.get("ABILITY_1"),
                3, hotkeyMap.get("ABILITY_2"),
                4, hotkeyMap.get("ULTIMATE"),
                5, hotkeyMap.get("INTERACT"),
                6, hotkeyMap.get("JUMP"),
                7, hotkeyMap.get("CROUCH"),
                8, hotkeyMap.get("MELEE"),
                9, hotkeyMap.get("RELOAD")
        );
    }

    /**
     * 노트 재생
     */
    public void playNote(long delay) {
        while(System.nanoTime() - startTime < delay); // 정확한 실행을 위한 반복 (1ms 미만 오차)
        while(!noteInfos.isEmpty()) {
            NoteInfo noteInfo = noteInfos.poll();

            while(noteInfo.nanoTime() < System.nanoTime() - startTime);
            if (noteInfo.isPress()) {
                robot.keyPress(noteInfo.keyCode());
            } else {
                robot.keyRelease(noteInfo.keyCode());
            }
        }
    }

    /**
     * BPM 변환 재생
     */
    public void playBPM() {
        while(!bpmInfos.isEmpty()) {
            BPMInfo bpmInfo = bpmInfos.poll();

            while(bpmInfo.nanoTime() < System.nanoTime() - startTime);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    /**
     * BPM 입력 (미완성)
     */
    public void inputBPM() { // TODO: 정상 동작하는지 확인 필요
        bpmInfos.forEach(bpmInfo -> {
            String num = new StringBuilder(Integer.toBinaryString(bpmInfo.bpm())).reverse().toString();
            for(int i = num.length() - 1; i >= 0; i--) {
                if(num.charAt(i) == '1') {
                    info(numToKeyMap.get(i));
                    if(i < 2)
                        pressMouse(robot, numToKeyMap.get(i));
                    else
                        pressKey(robot, numToKeyMap.get(i));
                }
            }
            robot.delay(500);
        });
    }

    private void pressKey(Robot robot, int keyCode) {
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }

    private void pressMouse(Robot robot, int mouseCode) {
        robot.mousePress(InputEvent.getMaskForButton(mouseCode));
        robot.mouseRelease(InputEvent.getMaskForButton(mouseCode));
    }
}
