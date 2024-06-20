package com.fruitcoding.owrhythmplayer.audio;

import it.sauronsoftware.jave.EncoderException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

class MusicFileConverterTest {
    /**
     * 파일 -> wav 변환 테스트
     */
    @Test
    void convertToWAV() {
        AudioFileConverter converter = AudioFileConverter.getInstance();
        info("user.dir: " + System.getProperty("user.dir"));

        try {
            converter.convertToWAV(System.getProperty("user.dir") + "/src/test/resources/test.mp3");
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(converter.getWavFile());
    }
}