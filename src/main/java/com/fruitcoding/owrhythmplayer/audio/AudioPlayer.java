package com.fruitcoding.owrhythmplayer.audio;

import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.LockSupport;

import static com.fruitcoding.owrhythmplayer.controller.MainController.startTime;
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

    /**
     * AudioPlayer에서 사용할  오디오 파일 추가
     * @throws UnsupportedAudioFileException 지원하지 않는 오디오 파일
     * @throws IOException 오디오 파일에 문제 발생 시
     * @throws LineUnavailableException 지원되지 않는 장치일 경우
     */
    private void initAudioClip() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        audioClip = AudioSystem.getClip(info);
        audioClip.open(ais);
    }

    /**
     * AudioPlayer 재생
     * @param delay 시작 딜레이
     * @param volume 음악 볼륨
     */
    public void play(long delay, float volume) { // TODO: 몇 곡 하다보면 음악 재생만 안되는 경우가 있음
        if(audioClip != null && !audioClip.isRunning()) {
            setVolume(volume);
            audioClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    startTime = 0L;
                }
            });
            new Thread(() -> {
                long delayLeft = 0;
                do {
                    delayLeft = delay + startTime - System.nanoTime();
                    if(delayLeft > 1_000_000) {
                        LockSupport.parkNanos(delayLeft - 1_000_000);
                    } else if(delayLeft > 10_000) {
                        // 짧은 주기로 대기 (정확도 높임)
                        LockSupport.parkNanos(delayLeft - 10_000);  // 10 마이크로초 대기
                    } else {
                        Thread.onSpinWait();  // CPU 친화적인 스핀 대기
                    }
                } while(delayLeft > 0);
                audioClip.start();
                info("Play time: " + (System.nanoTime() - startTime));
            }).start();
        } else {
            error("Not Playing");
        }
    }

    /**
     * 음악이 재생 중인지 확인
     * @return 음악 재생 여부
     */
    public boolean isPlaying() {
        return audioClip.isRunning();
    }

    /**
     * 음악이 일시정지인지 확인
     * @return 일시정지 여부
     */
    public boolean isPaused() { return !audioClip.isRunning() && audioClip.getFramePosition() > 0; }

    /**
     * 음악 일시정지
     */
    public void pause() {
        if(audioClip != null && audioClip.isRunning())
            audioClip.stop();
    }

    /**
     * 음악 중지
     */
    public void stop() {
        if(audioClip != null) {
            audioClip.stop();
            audioClip.setFramePosition(0);
        }
    }

    /**
     * 음악 강제 중지
     */
    public void shutdown() {
        scheduler.shutdown();
        if(audioClip != null)
            audioClip.close();
    }

    /**
     * 음악의 볼륨 설정
     * @param volume 음악 파일에 설정할 볼륨
     */
    private void setVolume(float volume) {
        FloatControl gainControl = (FloatControl)audioClip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float)(Math.log(volume / 100.0) / Math.log(10.0) * 20.0);
        dB = Math.max(dB, gainControl.getMinimum());
        gainControl.setValue(dB);
    }
}
