package com.snapmocklib.services.mock;

import com.snapmocklib.models.Mode;
import com.snapmocklib.services.AbstractAssert;
import com.snapmocklib.services.RecordService;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSnapMockLibTest extends AbstractAssert {

    private final List<MockMethod> mockReturns = new ArrayList<>();

    protected AbstractSnapMockLibTest() {
        super(new RecordService());
    }

    @BeforeEach
    protected void startTest(TestInfo testInfo) {
        recordService.startTest(getTestId(testInfo), Mode.AUTO, Map.of());
    }

    @BeforeEach
    protected void resetMocks() {
        mockReturns.clear();
    }

    public MockMethod when(String method) {
        MockMethod mockMethod = new MockMethod(method);
        mockReturns.add(mockMethod);
        return mockMethod;
    }

    public MockStatic mockStatic(Class<?> clazz) {
        return MockStatic.newInstance(
          recordService,
          clazz
        );
    }

    public <T> T mockStep(T obj) {
        return (T) MockStepProxy.newInstance(
          recordService,
          obj);
    }

    public <T> T mockOutput(Class<T> clazz) {
        return (T) MockOutputProxy.newInstance(
          recordService,
          clazz,
          mockReturns);
    }

    @AfterEach
    protected void endTest() throws IOException {
        recordService.endTest();
    }

    @AfterEach
    protected void expectAllMocksToHaveBeenCalled() {
        assertAllMocksToHaveBeenCalled();
    }

    protected String getTestId(TestInfo testInfo) {
        return this.getClass().getSimpleName() + "#" + testInfo.getTestMethod().orElseThrow().getName();
    }

    protected void assertAllMocksToHaveBeenCalled() {
        List<MockMethod> mockMethods = this.mockReturns.stream()
                .filter(mockMethod -> !mockMethod.isCalled())
                .collect(Collectors.toList());
        if (!mockMethods.isEmpty()) {
            Assertions.fail("Expect Mocks: [" +
                    mockMethods.stream()
                            .map(mockMethod -> mockMethod.getMethod() + recordService.getJsonConverter().toJson(mockMethod.getArgs()))
                            .collect(Collectors.joining(", ")) +
                    "] to be called, but they were not..."

            );
        }
    }

    public static class MockMethod {
        @Getter
        private final String method;
        @Getter
        private Object result;

        @Getter
        private Object[] args;

        private boolean isCalled = false;

        public MockMethod(String method) {
            this.method = method;
        }

        public boolean isCalled() {
            return this.isCalled;
        }

        public void setCalled() {
            this.isCalled = true;
        }

        public AbstractSnapMockLibTest.MockMethod withArgs(Object... args) {
            this.args = args;
            return this;
        }

        public void thenReturn(Object result) {
            this.result = result;
        }

        public void thenThrow(Throwable throwable) {
            this.result = throwable;
        }
    }
}
