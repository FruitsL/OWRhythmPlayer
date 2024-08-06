package com.fruitcoding.owrhythmplayer.map.osu;

import com.fruitcoding.owrhythmplayer.data.HotKeyMap;
import com.fruitcoding.owrhythmplayer.data.LineMap;
import com.fruitcoding.owrhythmplayer.map.base.BPMInfo;
import com.fruitcoding.owrhythmplayer.map.base.MapInfo;
import com.fruitcoding.owrhythmplayer.map.base.NoteInfo;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class OsuMapInfo extends MapInfo {
    private int lastBPM = -1;
    @Setter
    private int circleSize = 0;

    private LineMap lineMap = LineMap.getInstance();
    Map<String, Integer> hotkeyMap;
    /**
     * 로봇 생성
     *
     * @throws AWTException
     */
    public OsuMapInfo(int circleSize) throws AWTException, IOException {
        super();
        this.circleSize = circleSize;
        hotkeyMap = HotKeyMap.getInstance().getMap().entrySet().stream()
                .collect(Collectors.toMap(
                       Map.Entry::getValue, Map.Entry::getKey
                ));
    }

    @Override
    public void addNoteInfosByString(Object info) {
        String[] infos = info.toString().split(",");
        String[] additionInfos = infos[5].split(":");

        noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000, true, hotkeyMap.get(lineMap.getMap().get(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2))) ); // 현재는 라인 위치를 keyCode로 넣었지만 추후엔 라인 위치 기준 KeyCode로 변경해야함
        if(Integer.parseInt(additionInfos[0]) > 1) {
            noteInfos.add( new NoteInfo((Long.parseLong(infos[2]) + Integer.parseInt(additionInfos[0])) * 1_000_000, false, hotkeyMap.get(lineMap.getMap().get(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2))) );
        } else {
            noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000, false, hotkeyMap.get(lineMap.getMap().get(((Integer.parseInt(infos[0]) * circleSize / 256) - 1) / 2))) );
        }
    }

    @Override
    public void addBPMInfosByString(Object info) {
        String[] infos = info.toString().split(",");

        if(lastBPM < 0) {
            setInitBPM((int)Math.round(60_000 / Double.parseDouble(infos[1])));
            lastBPM = getInitBPM();
            return;
        }

        double timingPoint = Double.parseDouble(infos[1]);
        if(timingPoint < 0) {
            bpmInfos.add( new BPMInfo(Long.parseLong(infos[0]) * 1_000_000, (int)Math.round((-10_000) / timingPoint)) );
        } else {
            lastBPM = 60_000 / Integer.parseInt(infos[1]);
            bpmInfos.add(new BPMInfo(Long.parseLong(infos[0]) * 1_000_000, lastBPM * 100 / getInitBPM()));
        }
    }
}
