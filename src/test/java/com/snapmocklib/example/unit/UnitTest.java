package com.snapmocklib.example.unit;

import com.snapmocklib.example.classes.*;
import com.snapmocklib.example.models.EnumTest;
import com.snapmocklib.example.models.MyException;
import com.snapmocklib.example.models.SecondOutputResult;
import com.snapmocklib.models.Mode;
import com.snapmocklib.services.mock.AbstractSnapMockLibTest;
import com.snapmocklib.utils.PatternRegexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.Map;

class UnitTest extends AbstractSnapMockLibTest {

  private final MyApi myApi;

  UnitTest() {
    this.myApi = mockStep(
      new MyApiService(
        mockOutput(MyFirstOutputSpi.class),
        mockOutput(MySecondOutputSpi.class),
        mockOutput(MyThirdOutputSpi.class),
        new MyStepImpl()
      )
    );
  }

  @BeforeEach
  protected void startTest(TestInfo testInfo) {
    recordService.startTest(getTestId(testInfo), Mode.AUTO, Map.of(
      "uuidParam", PatternRegexUtils.UUID,
      "dateParam", PatternRegexUtils.DATETIME_WITH_NANO_ISO,
      "stackTrace", PatternRegexUtils.ANY
    ));
  }

  @Test
  void unitTestWithValue1() throws MyException {
    when("MyFirstOutputSpi.myFirstOutputMethod").thenReturn(EnumTest.VALUE1);
    when("MySecondOutputSpi.mySecondOutputMethod")
      .withArgs(0)
      .thenReturn(new SecondOutputResult(1));
    this.myApi.methodToTest(0);
    assertNoThrow("MyApi.methodToTest");
  }

  @Test
  void unitTestWithValue2() throws MyException {
    when("MyFirstOutputSpi.myFirstOutputMethod").thenReturn(EnumTest.VALUE2);
    when("MySecondOutputSpi.mySecondOutputMethod")
      .withArgs(0)
      .thenReturn(new SecondOutputResult(0));
    when("MySecondOutputSpi.mySecondOutputMethod")
      .withArgs(1)
      .thenReturn(new SecondOutputResult(1));
    this.myApi.methodToTest(0);
    assertNoThrow("MyApi.methodToTest");
  }

  @Test
  void unitTestWithNull() throws MyException {
    when("MyFirstOutputSpi.myFirstOutputMethod").thenReturn(null);
    when("MySecondOutputSpi.mySecondOutputMethod").thenReturn(null);
    this.myApi.methodToTest(0);
    assertNoThrow("MyApi.methodToTest");
  }

  @Test
  void unitTestWithException() throws MyException {
    when("MyFirstOutputSpi.myFirstOutputMethod").thenReturn(EnumTest.EXCEPTION);
    assertHasThrow(() -> this.myApi.methodToTest(0), MyException.class, "Exception occur during the test");
  }

  @Test
  void unitTestWithOutputException() throws MyException {
    when("MyFirstOutputSpi.myFirstOutputMethod").thenThrow(new MyException("Exception from the firstOutput"));
    assertHasThrow(() -> this.myApi.methodToTest(0), MyException.class, "Exception from the firstOutput");
  }
}
