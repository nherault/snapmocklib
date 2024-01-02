package com.snapmocklib.services.integration;

import com.snapmocklib.models.Mode;
import com.snapmocklib.services.AbstractAssert;
import com.snapmocklib.services.RecordService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractSnapMockLibIntegrationTest extends AbstractAssert {

    protected AbstractSnapMockLibIntegrationTest(RecordService recordService) {
        super(recordService);
    }

    @BeforeEach
    protected void startTest(TestInfo testInfo) {
        recordService.startTest(getTestId(testInfo), Mode.AUTO_PLAYBACK, Map.of());
    }

    @AfterEach
    protected void endTest() throws IOException {
        recordService.endTest();
    }

    protected String getTestId(TestInfo testInfo) {
        return this.getClass().getSimpleName() + "#" + testInfo.getTestMethod().orElseThrow().getName();
    }
}
