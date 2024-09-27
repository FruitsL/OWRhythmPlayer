package com.fruitcoding.owrhythmplayer.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Line {
    ABILITY_1(0, "ABILITY_1"),
    CROUCH(1, "CROUCH"),
    JUMP(2, "JUMP"),
    MELEE(3, "MELEE");

    // 필드 선언
    private final int keyCode;
    private final String keyName;

    // 코드로부터 Enum 상수를 찾는 메서드
    public static Line fromCode(int code) {
        for (Line line : Line.values()) {
            if (line.getKeyCode() == code) {
                return line;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}