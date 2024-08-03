package com.fruitcoding.owrhythmplayer.audio;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

@Getter
@Setter
public class AudioFileConverter {
    private static volatile AudioFileConverter instance;

    private File wavFile = new File(STR."\{System.getProperty("user.dir")}/data/music.wav");

    /**
     * 인스턴스를 반환하는 정적 메서드
     *
     * @return ConvertMusicFile instance
     */
    public static AudioFileConverter getInstance() {
        if (instance == null) {
            synchronized (AudioFileConverter.class) {
                if (instance == null) {
                    instance = new AudioFileConverter();
                }
            }
        }
        return instance;
    }

    /**
     * 오디오 파일을 wav 포맷으로 변환
     *
     * @param filePath wav로 변환할 오디오 파일 변경
     * @throws EncoderException wav로 변환 중 오류 발생 시
     */
    public void convertToWAV(String filePath) throws EncoderException {
        info(STR."Source : \{filePath}, Target : \{wavFile.getAbsolutePath()}");
        ProcessBuilder builder;
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            builder = new ProcessBuilder(STR."\{System.getProperty("user.dir")}/data/ffmpeg.exe", "-y", "-i", filePath, wavFile.getAbsolutePath());
        } else {
            builder = new ProcessBuilder("ffmpeg", "-y", "-i", filePath, wavFile.getAbsolutePath());
        }
        Process process = null;
        int exitCode;
        try {
            process = builder.start();
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (exitCode == 0) {
            System.out.println("Conversion successful");
        } else {
            System.out.println(STR."Conversion failed (exitCode : \{exitCode})");
        }
    }
}
