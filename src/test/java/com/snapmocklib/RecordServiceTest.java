package com.snapmocklib;

import com.snapmocklib.models.Mode;
import com.snapmocklib.services.RecordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

class RecordServiceTest {

    private final RecordService recordService = new RecordService();

    @Test
    void recordAndCheckMode(TestInfo testInfo) throws IOException {
        String testId = getTestId(testInfo);
        Path recordFile = getRepository(testId);
        Assertions.assertFalse(Files.exists(recordFile));

        // Record mode
        recordService.startTest(testId, Mode.RECORD, Map.of());
        boolean isFileRecorded = recordService.endTest();
        Assertions.assertTrue(isFileRecorded);
        Assertions.assertTrue(Files.exists(recordFile));

        // Check mode
        recordService.startTest(testId, Mode.CHECK, Map.of());
        isFileRecorded = recordService.endTest();
        Assertions.assertFalse(isFileRecorded);
        Assertions.assertTrue(Files.exists(recordFile));

        Files.delete(recordFile);
    }

    @Test
    void autoMode(TestInfo testInfo) throws IOException {
        String testId = getTestId(testInfo);
        Path recordFile = getRepository(testId);
        Assertions.assertFalse(Files.exists(recordFile));

        // Call First time to create the reference
        recordService.startTest(testId, Mode.AUTO, Map.of());
        boolean isFileRecorded = recordService.endTest();
        Assertions.assertTrue(isFileRecorded);
        Assertions.assertTrue(Files.exists(recordFile));

        // Call Second time to do the CHECK with the reference
        recordService.startTest(testId, Mode.AUTO, Map.of());
        isFileRecorded = recordService.endTest();
        Assertions.assertFalse(isFileRecorded);
        Assertions.assertTrue(Files.exists(recordFile));

        Files.delete(recordFile);
    }

    private Path getRepository(String testId) {
        return Path.of("src/test/resources/scenarios/" + testId.replace("#", "/") + ".json");
    }

    private String getTestId(TestInfo testInfo) {
        return this.getClass().getSimpleName() + "#" + testInfo.getTestMethod().orElseThrow().getName();
    }
}
