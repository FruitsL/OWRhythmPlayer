package com.fruitcoding.owrhythmplayer.file.osu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;
import static org.junit.jupiter.api.Assertions.*;

class OszFileTest {
    static OszFile oszFile = null;

    @BeforeAll
    static void init() throws OszFile.NotAZipFileException, IOException {
        oszFile = new OszFile(System.getProperty("user.dir") + "/1934192 Kaneko Chiharu - INF-B _L-aste-R_.osz");
        oszFile.getFileMap().forEach((k, v) -> {
            info("=== fileMap Key: " + k + " ===");
            v.forEach((k2, v2) -> {
                info(k2 + ": " + v2);
            });
        });
    }
    /**
     * 확장자로 파일 목록 찾기
     */
    @Test
    void getFileNameByExtension() {
        info("=== osu Files ===");
        for(String s : oszFile.getFileNameByExtension("osu")) {
            info(s);
        }
    }

    /**
     * 파일명으로 파일 찾기
     */
    @Test
    void getFileByName() {
        info("=== getFileByName() ===");
        info("Result: " + oszFile.getFileByName("audio.ogg").getPath());
    }
}