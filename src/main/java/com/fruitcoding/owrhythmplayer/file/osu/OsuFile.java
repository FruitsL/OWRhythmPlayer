package com.fruitcoding.owrhythmplayer.file.osu;

import com.fruitcoding.owrhythmplayer.file.base.MapFile;
import lombok.Getter;
import lombok.Setter;

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

    public OsuFile(File file) throws IOException {
        super();
        setFile(file);
        readFile();
    }
    public OsuFile(String filePath) throws IOException {
        super();
        setFilePath(filePath);
        readFile();
    }

    private void readFile() throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(getFile()))) {
            audioFileName = getContent(br, "AudioFilename:");
            circleSize = Integer.parseInt(getContent(br, "CircleSize:"));
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
        List<String> contents = new ArrayList<>();
        String line;

        while(!(line = br.readLine()).contains(content));
        return line;
    }
}
