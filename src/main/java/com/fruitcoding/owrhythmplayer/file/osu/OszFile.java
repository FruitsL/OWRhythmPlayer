package com.fruitcoding.owrhythmplayer.file.osu;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Setter
@Getter
public class OszFile {
    private File file = null;
    private Map<String, Map<String, File>> fileMap = null;
    private String tempDirectoryPath = System.getProperty("user.dir") + "/data/temp";

    public static class NotAZipFileException extends Exception {
        public NotAZipFileException(String message) {
            super(message);
        }
    }

    public OszFile(File file) throws NotAZipFileException, IOException {
        createTempDirectory(tempDirectoryPath);

        if(!file.getName().endsWith(".osz")) {
            throw new NotAZipFileException("The provided file is not a osz file.");
        }
        setFile(file);

        fileMap = new HashMap<>();
        unZip(file.toPath());
    }

    public OszFile(String filePath) throws NotAZipFileException, IOException {
        createTempDirectory(tempDirectoryPath);

        if(!filePath.endsWith(".osz")) {
            throw new NotAZipFileException("The provided file is not a osz file.");
        }
        setFile(new File(filePath));

        fileMap = new HashMap<>();
        unZip(Path.of(filePath));
    }

    /**
     * 압축해제 후 temp 폴더에 추가
     *
     * @param filePath 압축해제할 파일의 경로
     * @throws IOException
     */
    private void unZip(Path filePath) throws IOException {
        clearDirectory(Path.of(tempDirectoryPath));

        try(ZipInputStream zis = new ZipInputStream(Files.newInputStream(filePath))) {
            ZipEntry entry;

            while((entry = zis.getNextEntry()) != null) {
                if(!entry.isDirectory()) {
                    String fileName = entry.getName();
                    String fileExtension = getFileExtension(entry.getName());

                    if(!fileMap.containsKey(fileExtension)) {
                        fileMap.put(fileExtension, new HashMap<>());
                    }

                    File tempFile = new File(tempDirectoryPath, fileName);
                    try(OutputStream os = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while( (length = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    }

                    fileMap.get(fileExtension).put(fileName, tempFile);
                }
            }

        }
    }

    /**
     * 폴더 생성
     *
     * @throws IOException
     */
    private void createTempDirectory(String tempDirectoryPath) throws IOException {
        Path tempDirPath = Paths.get(tempDirectoryPath);
        if(!Files.exists(tempDirPath)) {
            Files.createDirectories(tempDirPath);
        }
    }

    /**
     * 폴더 내 모든 파일 삭제
     *
     * @param path 삭제할 경로
     * @throws IOException
     */
    private void clearDirectory(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(file -> {
                    if (!file.getAbsolutePath().equals(path.toFile().getAbsolutePath())) {
                        file.delete();
                    }
                });
    }

    /**
     * 파일명에서 확장자 가져오기
     *
     * @param fileName 파일명
     * @return
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return fileName.substring(lastDot + 1);
    }

    /**
     * 특정 확장자를 가진 파일 목록 가져오기
     *
     * @param extension 확장자
     * @return 해당 확장자를 가진 파일 목록
     */
    public List<String> getFileNameByExtension(String extension) {
        if(fileMap.containsKey(extension)) {
            return new ArrayList<>(fileMap.get(extension).keySet());
        }
        return Collections.emptyList();
    }

    /**
     * 파일명으로 File 가져오기
     * 
     * @param fileName 파일명
     * @return File
     */
    public File getFileByName(String fileName) {
        for(Map<String, File> files : fileMap.values()) {
            if(files.containsKey(fileName)) {
                return files.get(fileName);
            }
        }
        return null;
    }
}
