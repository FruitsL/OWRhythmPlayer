package com.fruitcoding.owrhythmplayer.file.osu;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

class OsuFileTest {
    @Test
    public void readContent() throws IOException {
        OsuFile oszFile = new OsuFile(System.getProperty("user.dir") + "/test.osu");

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