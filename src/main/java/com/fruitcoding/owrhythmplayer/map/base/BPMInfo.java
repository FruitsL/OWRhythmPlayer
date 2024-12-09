package com.fruitcoding.owrhythmplayer.map.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BPMInfo {
    long nanoTime;
    int bpm;

    // 생성자 정의
    public BPMInfo(long nanoTime, int bpm) {
        this.nanoTime = nanoTime;
        this.bpm = bpm;
    }

}
