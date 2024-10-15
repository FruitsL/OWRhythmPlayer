package com.fruitcoding.owrhythmplayer.audio;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import lombok.Getter;
import lombok.Setter;

import java.io.*;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.error;
import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

@Getter
@Setter
public class AudioFileConverter {
    private static volatile AudioFileConverter instance;

    private File wavFile = new File(System.getProperty("user.dir") + "/data/music.wav");

    /**
     * 인스턴스를 반환하는 정적 메서드
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
     * @param filePath wav로 변환할 오디오 파일 변경
     */
    public void convertToWAV(String filePath) {
        info("Source : " + filePath + ", Target : " + wavFile.getAbsolutePath());
        ProcessBuilder builder;
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            builder = new ProcessBuilder("./data/ffmpeg", "-y", "-loglevel", "error", "-i", filePath, wavFile.getAbsolutePath());
        } else {
            builder = new ProcessBuilder("ffmpeg", "-y", "-loglevel", "error", "-i", filePath, wavFile.getAbsolutePath());
        }
        Process process = null;
        int exitCode;
        try {
            process = builder.start();
            errorStream(process);

            exitCode = process.waitFor();
        } catch (Exception e) {
            error("Conversion failed: " + e);
            throw new RuntimeException(e);
        }
        if (exitCode == 0) {
            info("Conversion successful");
        } else {
            error("Conversion failed (exitCode : " + exitCode);
        }
    }

    /**
     * wav 변환 시 생성되는 에러 로그 출력 (미 출력 시 변환 불가)
     * @param process wav 파일로 변환하는 ffmpeg 프로세스
     */
    private void errorStream(Process process) {
        new Thread(() -> {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while((line = br.readLine()) != null) {
                    info(line);
                }
            } catch (Exception e) {
                error(e);
            }
        }).start();
    }
}
