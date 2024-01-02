package com.snapmocklib.example.integration;

import com.snapmocklib.example.classes.MyApi;
import com.snapmocklib.example.models.MyException;
import com.snapmocklib.models.Mode;
import com.snapmocklib.services.RecordService;
import com.snapmocklib.services.integration.AbstractSnapMockLibIntegrationTest;
import com.snapmocklib.utils.PatternRegexUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class IntegrationTest extends AbstractSnapMockLibIntegrationTest {

    private final MyApi myApi;

    @Autowired
    public IntegrationTest(RecordService recordService, MyApi myApi) {
        super(recordService);
        this.myApi = myApi;
    }

    @BeforeEach
    protected void startTest(TestInfo testInfo) {
        recordService.startTest(getTestId(testInfo), Mode.AUTO_PLAYBACK, Map.of(
          "uuidParam", PatternRegexUtils.UUID,
          "dateParam", PatternRegexUtils.DATETIME_WITH_NANO_ISO,
          "stackTrace", PatternRegexUtils.ANY
        ));
    }

    @Test
    void integrationTestWithStepExceptionTest() throws MyException {
        try {
            this.myApi.methodToTest(-1);
            Assertions.fail("Should be an exception");
        } catch (Exception e) {
            assertHasThrow("MyApiService.methodToTest", MyException.class, "Exception occur during the step");
        }
    }

    @Test
    void integrationTestWithExceptionDuringTest() throws MyException {
        try {
            this.myApi.methodToTest(0);
            Assertions.fail("Should be an exception");
        } catch (Exception e) {
            assertHasThrow("MyApiService.methodToTest", MyException.class, "Exception occur during the test");
        }
    }

    @Test
    void integrationTestWithValue1() throws MyException {
        this.myApi.methodToTest(1);
        assertNoThrow("MyApiService.methodToTest");
    }

    @Test
    void integrationTestWithValue2() throws MyException {
        this.myApi.methodToTest(2);
        assertNoThrow("MyApiService.methodToTest");
    }

    @Test
    void integrationTestWithFirstOutputException() throws MyException {
        try {
            this.myApi.methodToTest(4);
            Assertions.fail("Should be an exception");
        } catch (Exception e) {
            assertHasThrow("MyApiService.methodToTest", MyException.class, "Exception from first output");
        }
    }

    @Test
    void integrationTestWithSecondOutputException() throws MyException {
        try {
            this.myApi.methodToTest(5);
            Assertions.fail("Should be an exception");
        } catch (Exception e) {
            assertHasThrow("MyApiService.methodToTest", MyException.class, "Exception from second output");
        }
    }
}
