package com.snapmocklib.api;

import com.snapmocklib.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileRecordPersistence implements RecordPersistencePort {

    private String recordIdDirectorySeparator = "#";
    private String directory = "scenarios";

    public FileRecordPersistence() {}

    public FileRecordPersistence(String directory, String recordIdDirectorySeparator) {
        this.directory = directory;
        this.recordIdDirectorySeparator = recordIdDirectorySeparator;
    }

    @Override
    public void saveRecord(String recordId, String recordData) throws IOException {
        String filePath = getFilePath(recordId);
        File writeFile = new File(filePath);
        if (!writeFile.getParentFile().exists() && !writeFile.getParentFile().mkdirs()) {
            throw LogUtils.errorWithException("[ERROR] Cannot create directory: ${writeFile.parentFile.path}");
        }
        if (!writeFile.exists() && !writeFile.createNewFile()) {
            throw LogUtils.errorWithException("[ERROR] Cannot create file: ${writeFile.path}");
        }
        Files.write(Path.of(filePath), recordData.getBytes());
    }

    @Override
    public String getRecord(String recordId) throws IOException {
        return Files.readString(Path.of(getFilePath(recordId)));
    }

    private String getFilePath(String recordId) {
        return generatePath() + "/" + sanitizeFileName(recordId).replace(recordIdDirectorySeparator, "/") + ".json";
    }

    private String sanitizeFileName(String filename) {
        return filename.replace("[:\\\\/*\"?|<>']", "_");
    }

    private String generatePath() {
        String rootPath = System.getProperty("user.dir");
        return (rootPath + "/src/test/resources/" + directory).replace(
                "target/test-classes",
                "src/test/resources"
        );
    }
}
