package com.fruitcoding.owrhythmplayer.audio;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class AudioFileConverter {
    private static volatile AudioFileConverter instance;

    private File wavFile = new File(System.getProperty("user.dir") + "/data/music.wav");

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
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(new AudioAttributes());

        Encoder encoder = new Encoder();
        encoder.encode(new File(filePath), wavFile, attrs);
    }
}
