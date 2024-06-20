package com.fruitcoding.owrhythmplayer.map.osu;

import com.fruitcoding.owrhythmplayer.map.base.BPMInfo;
import com.fruitcoding.owrhythmplayer.map.base.MapInfo;
import com.fruitcoding.owrhythmplayer.map.base.NoteInfo;

import java.awt.*;

public class OszMapInfo extends MapInfo {
    private int lastBPM = 0;
    private int timingPoint = 100;
    /**
     * 로봇 생성
     *
     * @throws AWTException
     */
    protected OszMapInfo() throws AWTException {
        super();
    }

    @Override
    public void addNoteInfosByString(Object info) {
        String[] infos = info.toString().split(",");
        String[] additionInfos = infos[5].split(":");

        noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000, true, 'c') );
        if(Integer.parseInt(additionInfos[0]) > 1) {
            noteInfos.add( new NoteInfo((Long.parseLong(infos[2]) + Integer.parseInt(additionInfos[0])) * 1_000_000, false, 'c') );
        } else {
            noteInfos.add( new NoteInfo(Long.parseLong(infos[2]) * 1_000_000, false, 'c') );
        }
    }

    @Override
    public void addBPMInfosByString(Object info) {
        String[] infos = info.toString().split(",");

        if(bpmInfos.isEmpty()) {
            setInitBPM(60_000 / Integer.parseInt(infos[1]));
            lastBPM = getInitBPM();
            return;
        }

        timingPoint = Integer.parseInt(infos[1]);
        if(timingPoint < 0) {
            bpmInfos.add( new BPMInfo(Long.parseLong(infos[0]) * 1_000_000, (-100) / timingPoint) );
        } else {
            lastBPM = 60_000 / Integer.parseInt(infos[1]);
            bpmInfos.add(new BPMInfo(Long.parseLong(infos[0]) * 1_000_000, lastBPM * 100 / getInitBPM()));
        }
    }
}
