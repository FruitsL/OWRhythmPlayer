package com.fruitcoding.owrhythmplayer.audio;

import org.junit.jupiter.api.Test;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;

class AudioPlayerTest {
    /**
     * 2개의 재생 장치에 음악 재생 테스트
     *
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void startMusic() throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); // Test 종료 전 스케쥴 강제종료 방지

        File file = AudioFileConverter.getInstance().getWavFile();

        AudioDevice audioDevice = new AudioDevice();
        ArrayList<Mixer.Info> outputs = audioDevice.getSourceMixerInfos();
        outputs.forEach(i -> info(i.getName()));

        AudioPlayer player1 = new AudioPlayer(outputs.get(1), file);
        AudioPlayer player2 = new AudioPlayer(outputs.get(3), file);
        player1.play(1000L);
        player2.play(3000L);

        latch.await(15, TimeUnit.SECONDS); // n초 후 테스트 종료
    }

    /**
     * 음악 재생 이후 중지
     *
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void stopMusic() throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); // Test 종료 전 스케쥴 강제종료 방지

        File file = AudioFileConverter.getInstance().getWavFile();

        AudioDevice audioDevice = new AudioDevice();
        ArrayList<Mixer.Info> outputs = audioDevice.getSourceMixerInfos();
        outputs.forEach(i -> info(i.getName()));

        AudioPlayer player1 = new AudioPlayer(outputs.get(1), file);
        player1.play(1000L);

        Thread.sleep(7000L);
        player1.stop(); // n초 후 중지

        latch.await(15, TimeUnit.SECONDS); // n초 후 테스트 종료
    }

    /**
     * 음악 재생 후 일시 중지한 뒤 다시 재생
     *
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void pauseMusic() throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); // Test 종료 전 스케쥴 강제종료 방지

        File file = AudioFileConverter.getInstance().getWavFile();

        AudioDevice audioDevice = new AudioDevice();
        ArrayList<Mixer.Info> outputs = audioDevice.getSourceMixerInfos();
        outputs.forEach(i -> info(i.getName()));

        AudioPlayer player1 = new AudioPlayer(outputs.get(1), file);
        player1.play(1000L);

        Thread.sleep(7000L);
        player1.pause(); // n초 후 일시중지

        Thread.sleep(3000L);
        player1.play(0L); // n초 후 재생

        latch.await(15, TimeUnit.SECONDS); // n초 후 테스트 종료
    }
}