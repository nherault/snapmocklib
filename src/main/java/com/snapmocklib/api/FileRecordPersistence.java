package com.snapmocklib.api;

import com.snapmocklib.utils.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileRecordPersistence implements RecordPersistencePort {

    private String recordIdDirectorySeparator = "#";
    private String directory = "scenarios";
    private String charsetName = "UTF-8";

    public FileRecordPersistence() {}

    public FileRecordPersistence(String directory, String recordIdDirectorySeparator, String charsetName) {
        this.directory = directory;
        this.recordIdDirectorySeparator = recordIdDirectorySeparator;
        this.charsetName = charsetName;
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
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath), Charset.forName(this.charsetName))) {
            writer.write(recordData);
        }
    }

    @Override
    public String getRecord(String recordId) throws IOException {
        return Files.readString(Path.of(getFilePath(recordId)), Charset.forName(this.charsetName));
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
