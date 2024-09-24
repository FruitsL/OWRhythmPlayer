package com.fruitcoding.owrhythmplayer.map.base;

import com.fruitcoding.owrhythmplayer.audio.AudioPlayer;
import com.fruitcoding.owrhythmplayer.controller.MainController;
import com.fruitcoding.owrhythmplayer.data.HotKeyMap;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.fruitcoding.owrhythmplayer.controller.MainController.startTime;
import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.*;

abstract public class MapInfo {
    @Getter
    public Queue<NoteInfo> noteInfos = new PriorityQueue<>( );
    @Getter
    public Queue<BPMInfo> bpmInfos = new LinkedList<BPMInfo>();

    Robot robot = null;
    @Setter @Getter
    private int initBPM = 0;
    private boolean isNotePlay;

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
        numToKeyMap = new HashMap<>();
        numToKeyMap.put(0, Math.abs(hotkeyMap.get("PRIMARY_FIRE")));
        numToKeyMap.put(1, Math.abs(hotkeyMap.get("SECONDARY_FIRE")));
        numToKeyMap.put(2, hotkeyMap.get("ABILITY_1"));
        numToKeyMap.put(3, hotkeyMap.get("ABILITY_2"));
        numToKeyMap.put(4, hotkeyMap.get("ULTIMATE"));
        numToKeyMap.put(5, hotkeyMap.get("INTERACT"));
        numToKeyMap.put(6, hotkeyMap.get("JUMP"));
        numToKeyMap.put(7, hotkeyMap.get("CROUCH"));
        numToKeyMap.put(8, hotkeyMap.get("MELEE"));
        numToKeyMap.put(9, hotkeyMap.get("RELOAD"));
        numToKeyMap.put(10, hotkeyMap.get("WEAPON1"));
        numToKeyMap.put(11, hotkeyMap.get("WEAPON2"));
    }

    /**
     * 노트 재생
     */
    public void playNote(long initDelay) {
        isNotePlay = true;
        new Thread(() -> {
            long delay = initDelay * 1_000_000;
            info(STR."Note time: \{System.nanoTime() - startTime}, Queue Size: \{noteInfos.size()}");
            while(!noteInfos.isEmpty() && isNotePlay) {
                NoteInfo noteInfo = noteInfos.poll();
                if(noteInfo == null)
                    return;
                while(noteInfo.nanoTime() + startTime + delay > System.nanoTime());

                debug(STR."\{noteInfo.nanoTime()}, \{noteInfo.keyCode()}, \{noteInfo.isPress()}");

                if(!isNotePlay)
                    return;

                if (noteInfo.isPress()) {
                    robot.keyPress(noteInfo.keyCode());
                } else {
                    robot.keyRelease(noteInfo.keyCode());
                }
            }
        }).start();
    }

    public void stopNote() {
        isNotePlay = false;
        hotkeyMap.values().forEach(v -> {
            info(STR."hotkeyMap Release: \{v}");
            if(v >= 0)
                robot.keyRelease(v);
        });
    }

    /**
     * BPM 변환 재생
     */
    public void playBPM(long initDelay) {
        isNotePlay = true;
        new Thread(() -> {
            long delay = initDelay * 1_000_000;
            boolean weapon = false;
            while (!bpmInfos.isEmpty() && isNotePlay) {
                BPMInfo bpmInfo = bpmInfos.poll();

                while(bpmInfo.getNanoTime() + startTime + delay > System.nanoTime());
                if(!isNotePlay)
                    return;

                info(STR."BPM 변경 : \{weapon}");
                if(weapon) { // TODO: 제대로 변속 안바뀌는 증상 있음
                    pressKey(robot, numToKeyMap.get(10));
                    weapon = false;
                } else {
                    pressKey(robot, numToKeyMap.get(11));
                    weapon = true;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * BPM 입력 (미완성)
     */
    public void inputBPM() {
        pressKey(robot, numToKeyMap.get(11)); // START
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        bpmInfos.forEach(bpmInfo -> {
            info(STR."BPM 입력 : \{bpmInfo.getBpm()}");
            String num = new StringBuilder(Integer.toBinaryString(bpmInfo.getBpm())).reverse().toString();
            for(int i = num.length() - 1; i >= 0; i--) { // TODO: BPM이 매우 크면 가장 작은 값이 무시되는지 확인 필요
                if(num.charAt(i) == '1') {
                    info(STR."Press: \{numToKeyMap.get(i)}");
                    if(i < 2)
                        robot.mousePress(InputEvent.getMaskForButton(numToKeyMap.get(i)));
                    else
                        robot.keyPress(numToKeyMap.get(i));
                }
            }
            try {
                Thread.sleep(100); // robot.delay는 사용 불가
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for(int i = num.length() - 1; i >= 0; i--) {
                if (num.charAt(i) == '1') {
                    info(STR."Release: \{numToKeyMap.get(i)}");
                    if (i < 2)
                        robot.mouseRelease(InputEvent.getMaskForButton(numToKeyMap.get(i)));
                    else
                        robot.keyRelease(numToKeyMap.get(i));
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        pressKey(robot, numToKeyMap.get(10)); // END
        info("BPM 입력 완료");
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
