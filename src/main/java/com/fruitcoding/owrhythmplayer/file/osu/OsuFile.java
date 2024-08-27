package com.fruitcoding.owrhythmplayer.file.osu;

import com.fruitcoding.owrhythmplayer.file.base.MapFile;
import com.fruitcoding.owrhythmplayer.map.osu.OsuMapInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.*;

@Setter
@Getter
public class OsuFile extends MapFile {
    private List<String> timingPoints = null;
    private List<String> hitObjects = null;
    private String audioFileName = null;
    private int circleSize = 4;
    private OsuMapInfo osuMapInfo;

    @Builder
    public OsuFile(String filePath, File file) throws IOException, AWTException {
        super();
        if (filePath.isBlank()) {
            setFile(file);
        } else {
            setFilePath(filePath);
        }
        readFile();
        osuMapInfo = new OsuMapInfo(circleSize);
    }

    private void readFile() throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(getFile()))) {
            audioFileName = getContent(br, "AudioFilename:").replace("AudioFilename: ", "").trim();
            circleSize = Integer.parseInt(getContent(br, "CircleSize:")
                    .replace("CircleSize:", "").trim());
            timingPoints = getContents(br, "[TimingPoints]");
            hitObjects = getContents(br, "[HitObjects]");
        }
    }

    private List<String> getContents(BufferedReader br, String content) throws IOException {
        List<String> contents = new ArrayList<>();
        String line;

        while(!br.readLine().contains(content));
        while( (line = br.readLine()) != null) {
            if (line.isBlank()) {
                break;
            } else {
                contents.add(line);
            }
        }
        return contents;
    }

    private String getContent(BufferedReader br, String content) throws IOException {
        String line;

        while(!(line = br.readLine()).contains(content));
        return line;
    }
}
