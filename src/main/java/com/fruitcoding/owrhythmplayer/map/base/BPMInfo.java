package com.fruitcoding.owrhythmplayer.map.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BPMInfo {
    long nanoTime;
    int bpm;
}
