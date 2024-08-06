package com.fruitcoding.owrhythmplayer.file.osu;

import com.fruitcoding.owrhythmplayer.map.base.BPMInfo;
import com.fruitcoding.owrhythmplayer.map.osu.OsuMapInfo;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

class OsuFileTest {
    @Test
    public void readContent() throws IOException, AWTException {
        OsuFile oszFile = OsuFile.builder()
                .filePath(STR."\{System.getProperty("user.dir")}/test/Kaneko Chiharu - INF-B L-aste-R (ML-ysg) [NOVICE].osu")
                .build();
        OsuMapInfo osuMapInfo = new OsuMapInfo(oszFile.getCircleSize());

        info("=== getTimingPoints() ===");
        for(String s : oszFile.getTimingPoints()){
            info(s);
            osuMapInfo.addBPMInfosByString(s);
        }

        info("=== getHitObjects() ===");
        for(String s : oszFile.getHitObjects()) {
            info(s);
            osuMapInfo.addNoteInfosByString(s);
        }

        info("=== AudioFilename: ===");
        info(oszFile.getAudioFileName());

        info("=== Osu Map Info ===");
        info(STR."- bpmInfos: \{osuMapInfo.getBpmInfos().size()}");
        while(!osuMapInfo.getBpmInfos().isEmpty()) {
            info(osuMapInfo.getBpmInfos().poll());
        }

        info(STR."- noteInfos: \{osuMapInfo.getNoteInfos().size()}");
        while(!osuMapInfo.getNoteInfos().isEmpty()) {
            info(osuMapInfo.getNoteInfos().poll());
        }
    }
}