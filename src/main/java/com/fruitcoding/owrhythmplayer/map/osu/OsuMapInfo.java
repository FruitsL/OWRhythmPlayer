package com.fruitcoding.owrhythmplayer.map.osu;

import com.fruitcoding.owrhythmplayer.map.base.BPMInfo;
import com.fruitcoding.owrhythmplayer.map.base.MapInfo;
import com.fruitcoding.owrhythmplayer.map.base.NoteInfo;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.info;
import static java.util.Arrays.stream;

public class OsuMapInfo extends MapInfo {
    private int lastBPM = -1;
    @Setter
    private int circleSize = 0;

    Map<Integer, String> lineMap = Map.of(
            0, "ABILITY_1",
            1, "CROUCH",
            2, "JUMP",
            3, "MELEE"
    );
    /**
     * 로봇 생성
     *
     * @throws AWTException
     */
    public OsuMapInfo(int circleSize) throws AWTException, IOException {
        super();
        this.circleSize = circleSize;
    }

    @Override
    public void addNoteInfosByString(Object info) {
        String[] infos = info.toString().split(",");
        String[] additionInfos = infos[5].split(":");

        noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000, true, hotkeyMap.get(lineMap.get(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2))) ); // 현재는 라인 위치를 keyCode로 넣었지만 추후엔 라인 위치 기준 KeyCode로 변경해야함
        if(Integer.parseInt(additionInfos[0]) > 10) {
            noteInfos.add( new NoteInfo(Long.parseLong(additionInfos[0]) * 1_000_000 - 1_000, false, hotkeyMap.get(lineMap.get(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2))) );
        } else {
            noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000 + 1_000, false, hotkeyMap.get(lineMap.get(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2))) ); // 1ms 뒤에 떼기
        }
    }

    @Override
    public void addBPMInfosByString(Object info) { // TODO: 변속 -> 속도 순서로 BPM 변경 시 변속이 씹히고 속도만 반영되는 문제 있음
        String[] infos = info.toString().split(",");

        if(lastBPM < 0) {
            setInitBPM((int)Math.round(60_000 / Double.parseDouble(infos[1])));
            lastBPM = getInitBPM();
            bpmInfos.add(new BPMInfo((long)Double.parseDouble(infos[0]) * 1_000_000, lastBPM * 100 / getInitBPM()));
            return;
        }

        BPMInfo lastBPMInfo = ((LinkedList<BPMInfo>)bpmInfos).peekLast();
        double beatLength = Double.parseDouble(infos[1]);
        if((lastBPMInfo == null) || (Long.parseLong(infos[0]) * 1_000_000 - lastBPMInfo.getNanoTime() >= 5 * 1_000_000)) { // TODO: 코드 줄이기 필요
            if (beatLength < 0) {
                bpmInfos.add(new BPMInfo(Long.parseLong(infos[0]) * 1_000_000, (int) Math.round((-10_000) / beatLength)));
            } else {
                lastBPM = 60_000 / (int)Double.parseDouble(infos[1]);
                bpmInfos.add(new BPMInfo(Long.parseLong(infos[0]) * 1_000_000, lastBPM * 100 / getInitBPM()));
            }
        } else {
            if (beatLength < 0) {
                lastBPMInfo.setBpm((int) Math.round((-10_000) / beatLength));
            } else {
                lastBPM = 60_000 / (int)Double.parseDouble(infos[1]);
                lastBPMInfo.setBpm(lastBPM * 100 / getInitBPM());
            }
        }
    }
}
