package com.snapmocklib.example.integration;

import com.snapmocklib.example.classes.MyApi;
import com.snapmocklib.example.models.MyException;
import com.snapmocklib.models.Mode;
import com.snapmocklib.services.RecordService;
import com.snapmocklib.services.integration.AbstractSnapMockLibIntegrationTest;
import com.snapmocklib.utils.PatternRegexUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

@SpringBootTest
class IntegrationModeTest extends AbstractSnapMockLibIntegrationTest {

    private static final Map<String, Pattern> FIELDS_TO_REPLACE = Map.of(
      "uuidParam", PatternRegexUtils.UUID,
      "dateParam", PatternRegexUtils.DATETIME_WITH_NANO_ISO,
      "stackTrace", PatternRegexUtils.ANY
    );

    private final MyApi myApi;

    @Autowired
    public IntegrationModeTest(RecordService recordService, MyApi myApi) {
        super(recordService);
        this.myApi = myApi;
    }

    @BeforeEach
    protected void startTest(TestInfo testInfo) {

    }

    @AfterEach
    protected void endTest() {

    }

    @Test
    void integrationModeTestRecordPlayback(TestInfo testInfo) throws MyException, IOException {
        String testId = getTestId(testInfo);
        Path recordFile = getRepository(testId);

        // Clean in cass the previous test failed
        safeDeleteFile(recordFile);

        // Record the snapshot reference
        recordService.startTest(getTestId(testInfo), Mode.RECORD, FIELDS_TO_REPLACE);
        this.myApi.methodToTest(1);

        boolean isFileRecorded = recordService.endTest();

        Assertions.assertTrue(isFileRecorded);
        Assertions.assertTrue(Files.exists(recordFile));

        // Playback the file
        recordService.startTest(getTestId(testInfo), Mode.PLAYBACK, FIELDS_TO_REPLACE);
        this.myApi.methodToTest(1);

        isFileRecorded = recordService.endTest();

        Assertions.assertFalse(isFileRecorded);

        Files.delete(recordFile);
    }

    @Test
    void integrationModeTestAutoPlayback(TestInfo testInfo) throws MyException, IOException {
        String testId = getTestId(testInfo);
        Path recordFile = getRepository(testId);

        // Clean in case the previous test failed
        safeDeleteFile(recordFile);

        // Auto Record the snapshot reference
        recordService.startTest(getTestId(testInfo), Mode.AUTO_PLAYBACK, FIELDS_TO_REPLACE);
        this.myApi.methodToTest(1);

        boolean isFileRecorded = recordService.endTest();

        Assertions.assertTrue(isFileRecorded);
        Assertions.assertTrue(Files.exists(recordFile));

        // Auto Playback the file
        recordService.startTest(getTestId(testInfo), Mode.AUTO_PLAYBACK, FIELDS_TO_REPLACE);
        this.myApi.methodToTest(1);

        isFileRecorded = recordService.endTest();

        Assertions.assertFalse(isFileRecorded);

        Files.delete(recordFile);
    }

    private Path getRepository(String testId) {
        return Path.of("src/test/resources/scenarios/" + testId.replace("#", "/") + ".json");
    }

    private void safeDeleteFile(Path file) {
        try {
            Files.delete(file);
        } catch (Exception e) {
            // Nothing to do
        }
    }
}
