package com.fruitcoding.owrhythmplayer.util;

import com.fruitcoding.owrhythmplayer.controller.MainController;
import com.fruitcoding.owrhythmplayer.data.HotKeyMap;
import lombok.Getter;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.keyboard.SwingKeyAdapter;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.*;

public class GlobalKeyMouseListener extends SwingKeyAdapter {
    @Getter
    private HotKeyMap hotKeyMap = HotKeyMap.getInstance();
    private final Map<Integer, Boolean> keyPressedMap = resetKeyPressedMap();
    Map<Integer, Integer> mouseConvertMap = Map.of(
            0, 0, // None
            1, 1, // Left
            2, 3, // Middle
            3, 2 // Right
    );

    public Map<Integer, Boolean> resetKeyPressedMap() {
        return hotKeyMap.getMap().entrySet()
                .stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        _ -> false
                ));
    }

    public GlobalKeyMouseListener(MainController mainController) throws NativeHookException, IOException {
        LogManager.getLogManager().reset(); // 로그 비활성화
        GlobalScreen.registerNativeHook();

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {}

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                int keyCode = getJavaKeyEvent(nativeKeyEvent).getKeyCode();
                debug(STR."Pressed: \{keyCode}");
                if(!keyPressedMap.containsKey(keyCode))
                    return;
                if(keyPressedMap.get(keyCode))
                    return;

                keyPressedMap.replace(keyCode, true);
                switch(hotKeyMap.getMap().get(keyCode)) {
                    case "PLAY":
                        info("Button: PLAY");
                        try {
                            mainController.play();
                        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                            error(STR."play error.\n\{e}");
                        } catch (AWTException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "STOP":
                        info("Button: STOP");
                        mainController.pause();
                        break;
                    default:
                        break;
                }

                info(STR."nativeKeyPressed: \{NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode())} = \{KeyEvent.getKeyText(getJavaKeyEvent(nativeKeyEvent).getKeyCode())}\n"
                        + STR."KeyEvent = \{nativeKeyEvent.getKeyCode()}, NativeKeyEvent = \{getJavaKeyEvent(nativeKeyEvent).getKeyCode()}");
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
                int keyCode = getJavaKeyEvent(nativeKeyEvent).getKeyCode();
                debug(STR."Released: \{keyCode}");
                if(!keyPressedMap.containsKey(keyCode))
                    return;
                if(!keyPressedMap.get(keyCode))
                    return;

                keyPressedMap.replace(keyCode, false);
            }
        });

        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
            @Override
            public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {}

            @Override
            public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
//                info(STR."nativeMousePressed: \{nativeMouseEvent.getButton()} (\{mouseConvertMap.get(nativeMouseEvent.getButton())})");
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {}
        });
    }
}
