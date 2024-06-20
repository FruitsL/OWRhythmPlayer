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

    public void play(long delay) {
        long time = System.nanoTime();
        delay *= 1_000_000;
        if(audioClip != null && !audioClip.isRunning()) {
            while(System.nanoTime() - time < delay); // 정확한 실행을 위한 반복 (1ms 미만 오차)
            audioClip.start();
            info("Play time: " + (System.nanoTime() - time));
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
}
