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

    /**
     * 파일 또는 파일 경로로 파일을 받아서 필요한 내용 수집
     * @param filePath 파일 뎡로
     * @param file 파일
     * @throws IOException 파일이 없을 경우
     * @throws AWTException UI에 문제 발생할 경우
     */
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

    /**
     * 파일을 읽어서 필요한 정보 추가
     * @throws IOException 파일이 없을 경우
     */
    private void readFile() throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(getFile()))) {
            audioFileName = getContent(br, "AudioFilename:").replace("AudioFilename: ", "").trim();
            circleSize = Integer.parseInt(getContent(br, "CircleSize:")
                    .replace("CircleSize:", "").trim());
            timingPoints = getContents(br, "[TimingPoints]");
            hitObjects = getContents(br, "[HitObjects]");
        }
    }

    /**
     * content가 포함된 라인 이후부터 빈 라인까지 가져오기
     * @param br 버퍼 리더
     * @param content 탐색할 내용
     * @return content 이후부터 빈 라인 전까지
     * @throws IOException 탐색할 파일이 없을 경우
     */
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

    /**
     * content가 포함된 라인 가져오기
     * @param br 버퍼 리더
     * @param content 탐색할 내용
     * @return context가 포함된 라인
     * @throws IOException 탐색할 파일이 없을 경우
     */
    private String getContent(BufferedReader br, String content) throws IOException {
        String line;

        while(!(line = br.readLine()).contains(content));
        return line;
    }
}
