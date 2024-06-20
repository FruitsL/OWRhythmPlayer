package com.fruitcoding.owrhythmplayer.file.base;

import lombok.Data;

import java.io.File;

@Data
abstract public class MapFile {
    File file = null;

    /**
     * 파일 경로로 File 설정
     * @param filePath 파일 경로
     */
    public void setFilePath(String filePath) {
        file = new File(filePath);
    }
}
