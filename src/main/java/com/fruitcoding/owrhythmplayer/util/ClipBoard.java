package com.fruitcoding.owrhythmplayer.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

public class ClipBoard {
    // 클래스 내부에 유일한 인스턴스를 저장하는 변수
    private static ClipBoard instance;

    Robot robot;

    private ClipBoard() throws AWTException {
        robot = new Robot();
    }

    // 인스턴스를 얻기 위한 정적 메서드
    public static synchronized ClipBoard getInstance() throws AWTException {
        if (instance == null)
            instance = new ClipBoard();

        return instance;
    }

    public void copyToClipBoard(String text) {
        // 클립보드에 문자열을 저장하기 위한 StringSelection 객체 생성
        StringSelection stringSelection = new StringSelection(text);

        // 시스템 클립보드를 가져오기
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 클립보드에 문자열을 복사
        clipboard.setContents(stringSelection, null);
    }

    public void paste() {
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(100);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
        robot.delay(100);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        info("Paste Complete");
    }
}
