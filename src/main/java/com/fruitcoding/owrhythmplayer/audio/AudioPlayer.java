package com.fruitcoding.owrhythmplayer.audio;

import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.*;

@Setter
public class AudioPlayer {
    private Mixer.Info info;
    private File file;
    private ScheduledExecutorService scheduler;
    @Getter
    private Clip audioClip;

    public AudioPlayer(Mixer.Info info, File file) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        this.info = info;
        this.file = file;
        initAudioClip();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private void initAudioClip() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        audioClip = AudioSystem.getClip(info);
        audioClip.open(ais);
    }

    public void play(long delay, float volume) {
        long time = System.nanoTime();
        delay *= 1_000_000;
        if(audioClip != null && !audioClip.isRunning()) {
            setVolume(volume);
            while(System.nanoTime() - time < delay); // 정확한 실행을 위한 반복 (1ms 미만 오차)
            audioClip.start();
            info(STR."Play time: \{System.nanoTime() - time}");
        } else {
            error("Not Playing");
        }
    }

    public void pause() {
        if(audioClip != null && audioClip.isRunning())
            audioClip.stop();
    }

    public void stop() {
        if(audioClip != null) {
            audioClip.stop();
            audioClip.setFramePosition(0);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        if(audioClip != null)
            audioClip.close();
    }

    private void setVolume(float volume) {
        FloatControl gainControl = (FloatControl)audioClip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float)(Math.log(volume / 100.0) / Math.log(10.0) * 20.0);
        dB = Math.max(dB, gainControl.getMinimum());
        gainControl.setValue(dB);
    }
}
