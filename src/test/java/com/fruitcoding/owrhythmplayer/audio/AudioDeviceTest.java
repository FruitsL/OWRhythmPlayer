package com.fruitcoding.owrhythmplayer.audio;

import org.junit.jupiter.api.Test;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

class AudioDeviceTest {
    @Test
    void getAudioDevices() {
        AudioDevice audioDevice = new AudioDevice();
        info("getSourceMixerInfos(): 출력 장치 목록");
        audioDevice.getSourceMixerInfos().forEach(i -> info(i.getName()));
        info("getTargetMixerInfos(): 입력 장치 목록");
        audioDevice.getTargetMixerInfos().forEach(i -> info(i.getName()));
    }
}