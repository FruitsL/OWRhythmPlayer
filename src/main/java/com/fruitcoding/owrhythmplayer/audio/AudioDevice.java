package com.fruitcoding.owrhythmplayer.audio;

import lombok.Getter;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.fruitcoding.owrhythmplayer.util.LambdaExceptionUtil.rethrowPredicate;

@Getter
public class AudioDevice {
    private static volatile AudioDevice instance;

    ArrayList<Mixer.Info> sourceMixerInfos = null;
    ArrayList<Mixer.Info> targetMixerInfos = null;

    /**
     * 인스턴스를 반환하는 정적 메서드
     * @return AudioDevices instance
     */
    public static AudioDevice getInstance() {
        if (instance == null) {
            synchronized (AudioDevice.class) {
                if (instance == null) {
                    instance = new AudioDevice();
                }
            }
        }
        return instance;
    }

    AudioDevice() {
        setSourceMixerInfos();
        setTargetMixerInfos();
    }

    /**
     * 재생 장치 목록 가져오기 (@Getter)
     * @return 재생 장치 목록
     */
    public List<String> getSourceMixerInfosNameList() {
        return getSourceMixerInfos().stream()
                .map(Mixer.Info::getName)
                .collect(Collectors.toList());
    }

    /**
     * 재생 장치 목록 불러오기 (@Setter)
     */
    public void setSourceMixerInfos() {
        sourceMixerInfos = Arrays.stream(AudioSystem.getMixerInfo())
                .map(AudioSystem::getMixer)
                .flatMap(mixer -> Arrays.stream(mixer.getSourceLineInfo())
                        .filter(rethrowPredicate(lineInfo -> mixer.getLine(lineInfo) instanceof SourceDataLine && mixer.isLineSupported(lineInfo)))
                        .map(_ -> mixer.getMixerInfo()))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 출력 장치 목록 가져오기 (@Getter)
     * @return 출력 장치 목록
     */
    public List<String> getTargetMixerInfosNameList() {
        return getTargetMixerInfos().stream()
                .map(Mixer.Info::getName)
                .collect(Collectors.toList());
    }

    /**
     * 출력 장치 목록 불러오기 (@Setter)
     */
    public void setTargetMixerInfos() {
        targetMixerInfos = Arrays.stream(AudioSystem.getMixerInfo())
                .map(AudioSystem::getMixer)
                .flatMap(mixer -> Arrays.stream(mixer.getTargetLineInfo())
                        .filter(rethrowPredicate(lineInfo -> mixer.getLine(lineInfo) instanceof TargetDataLine && mixer.isLineSupported(lineInfo)))
                        .map(_ -> mixer.getMixerInfo()))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
