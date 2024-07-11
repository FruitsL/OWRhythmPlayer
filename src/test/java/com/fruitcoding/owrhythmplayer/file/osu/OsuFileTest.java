package com.fruitcoding.owrhythmplayer.file.osu;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

class OsuFileTest {
    @Test
    public void readContent() throws IOException {
        OsuFile oszFile = OsuFile.builder()
                .filePath(System.getProperty("user.dir") + "/1934192 Kaneko Chiharu - INF-B _L-aste-R_/Kaneko Chiharu - INF-B L-aste-R (ML-ysg) [NOVICE].osu")
                .build();

        info("=== getTimingPoints() ===");
        for(String s : oszFile.getTimingPoints()){
            info(s);
        }

        info("=== getHitObjects() ===");
        for(String s : oszFile.getHitObjects()) {
            info(s);
        }

        info("=== AudioFilename: ===");
        info(oszFile.getAudioFileName());
    }
}