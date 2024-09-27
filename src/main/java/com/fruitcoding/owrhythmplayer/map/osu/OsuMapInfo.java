package com.fruitcoding.owrhythmplayer.map.osu;

import com.fruitcoding.owrhythmplayer.data.enums.Line;
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
    private int lastBPM = 100;
    private int lastSpeed = 1;
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

        noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000, true, reverseHotkeyMap.get(Line.fromCode(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2).getKeyName())) );
        if(Integer.parseInt(additionInfos[0]) > 10) {
            noteInfos.add( new NoteInfo(Long.parseLong(additionInfos[0]) * 1_000_000 - 1_000, false, reverseHotkeyMap.get(Line.fromCode(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2).getKeyName())) );
        } else {
            noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000 + 1_000, false, reverseHotkeyMap.get(Line.fromCode(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2).getKeyName())) ); // 1ms 뒤에 떼기
        }
    }

    @Override
    public void addBPMInfosByString(Object info) {
        String[] infos = info.toString().split(",");
        double beatLength = Double.parseDouble(infos[1]);

        if(beatLength > 0) { // BPM
            if(getInitBPM() == 0) { // 첫 번째 BPM
                setInitBPM((int)Math.round(60_000 / beatLength));
                lastBPM = getInitBPM();
            } else {
                lastBPM = (int)(60_000 / beatLength);
                lastSpeed = 1;
            }
        } else { // Speed
            lastSpeed = (int) Math.round((-100) / beatLength);
        }

        BPMInfo lastBPMInfo = ((LinkedList<BPMInfo>)bpmInfos).peekLast();
        if((lastBPMInfo == null) || (Long.parseLong(infos[0]) * 1_000_000 - lastBPMInfo.getNanoTime() >= 5 * 1_000_000)) { // 신규 BPM 추가
            bpmInfos.add(new BPMInfo((long)Double.parseDouble(infos[0]) * 1_000_000, 1 + lastSpeed * lastBPM * 100 / getInitBPM()));
        } else { // 기존 BPM 변경
            lastBPMInfo.setBpm(1 + lastSpeed * lastBPM * 100 / getInitBPM());
        }
    }
}
