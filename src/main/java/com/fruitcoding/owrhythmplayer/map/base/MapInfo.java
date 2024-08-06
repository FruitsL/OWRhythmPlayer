package com.fruitcoding.owrhythmplayer.map.base;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.LinkedList;
import java.util.Queue;

abstract public class MapInfo {
    @Getter
    public Queue<NoteInfo> noteInfos = new LinkedList<NoteInfo>();
    @Getter
    public Queue<BPMInfo> bpmInfos = new LinkedList<BPMInfo>();

    Robot robot = null;
    @Setter
    private long initTime = 0;
    @Setter @Getter
    private int initBPM = 0;

    public abstract void addNoteInfosByString(Object info);
    public abstract void addBPMInfosByString(Object info);

    /**
     * 로봇 생성
     *
     * @throws AWTException
     */
    protected MapInfo() throws AWTException {
        robot = new Robot();
    }

    /**
     * 노트 재생
     */
    public void playNote() {
        while(!noteInfos.isEmpty()) {
            NoteInfo noteInfo = noteInfos.poll();

            while(noteInfo.nanoTime() < System.nanoTime() - initTime);
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

            while(bpmInfo.nanoTime() < System.nanoTime() - initTime);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }
}
