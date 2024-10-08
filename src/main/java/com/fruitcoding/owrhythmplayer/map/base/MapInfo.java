package com.fruitcoding.owrhythmplayer.map.base;

import com.fruitcoding.owrhythmplayer.data.json.HotKeyMap;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.LockSupport;

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

    public Map<String, Integer> reverseHotkeyMap;
    Map<Integer, Integer> numToKeyMap;

    /**
     * 로봇 생성
     *
     * @throws AWTException
     */
    protected MapInfo() throws AWTException, IOException {
        robot = new Robot();
        reverseHotkeyMap = HotKeyMap.getInstance().getReverseMap();
        numToKeyMap = new HashMap<>();
        numToKeyMap.put(0, Math.abs(reverseHotkeyMap.get("PRIMARY_FIRE")));
        numToKeyMap.put(1, Math.abs(reverseHotkeyMap.get("SECONDARY_FIRE")));
        numToKeyMap.put(2, reverseHotkeyMap.get("ABILITY_1"));
        numToKeyMap.put(3, reverseHotkeyMap.get("ABILITY_2"));
        numToKeyMap.put(4, reverseHotkeyMap.get("ULTIMATE"));
        numToKeyMap.put(5, reverseHotkeyMap.get("INTERACT"));
        numToKeyMap.put(6, reverseHotkeyMap.get("JUMP"));
        numToKeyMap.put(7, reverseHotkeyMap.get("CROUCH"));
        numToKeyMap.put(8, reverseHotkeyMap.get("MELEE"));
        numToKeyMap.put(9, reverseHotkeyMap.get("RELOAD"));
        numToKeyMap.put(10, reverseHotkeyMap.get("WEAPON1"));
        numToKeyMap.put(11, reverseHotkeyMap.get("WEAPON2"));
    }

    /**
     * 노트 재생
     */
    public void playNote(long initDelay) {
        isNotePlay = true;
        new Thread(() -> {
            long delay = initDelay * 1_000_000;
            info(STR."Note time: \{System.nanoTime() - startTime}, Queue Size: \{noteInfos.size()}");
            long delayLeft = 0;
            while(!noteInfos.isEmpty() && isNotePlay) {
                NoteInfo noteInfo = noteInfos.poll();
                if(noteInfo == null)
                    return;
                do {
                    delayLeft = noteInfo.nanoTime() + startTime + delay - System.nanoTime();
                    if(delayLeft > 1_000_000) {
                        LockSupport.parkNanos(delayLeft - 1_000_000);
                    } else if(delayLeft > 10_000) {
                        // 짧은 주기로 대기 (정확도 높임)
                        LockSupport.parkNanos(delayLeft - 10_000);  // 10 마이크로초 대기
                    } else {
                        Thread.onSpinWait();  // CPU 친화적인 스핀 대기
                    }
                } while(delayLeft > 0);

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
        reverseHotkeyMap.values().forEach(v -> {
            info(STR."hotkeyMap Release: \{v}");
            if(v >= 0)
                robot.keyRelease(v);
        });
    }

    /**
     * BPM 변환 재생
     */
    public void playBPM(long initDelay) { // TODO: 변속 많은 채보 실행 후 노래와 변속이 안나오는 증상이 있음
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
                if(weapon) {
                    pressKey(robot, numToKeyMap.get(10));
                    weapon = false;
                } else {
                    pressKey(robot, numToKeyMap.get(11));
                    weapon = true;
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
