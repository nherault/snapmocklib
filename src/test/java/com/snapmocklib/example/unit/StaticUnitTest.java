package com.snapmocklib.example.unit;

import com.snapmocklib.services.mock.AbstractSnapMockLibTest;
import com.snapmocklib.services.mock.MockStatic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class StaticUnitTest extends AbstractSnapMockLibTest {

  final MockStatic mockStatic = mockStatic(MyMapper.class);

  @Test
  void staticUnitTest() throws Throwable {
    Object[] args = {new ObjectToMap(
      "myString",
      0,
      50.1,
      Collections.singletonList(new ObjectToMap("myString", 0, 50.1, Collections.emptyList(), null)),
      new ObjectToMap("myString", 0, 50.1, Collections.emptyList(), null)
    )};
    mockStatic.invokeStatic("map", args);
    assertNoThrow("MyMapper.map");
  }

  @Test
  void noSuchMethodException() {
    try {
      mockStatic.invokeStatic("unknownMethod", (Object) null);
      Assertions.fail();
    } catch (Throwable e) {
      assertHasNotBeenCalled("unknownMethod");
    }
  }

  private static class MyMapper {

    static ObjectMapped map(ObjectToMap objectToMap) {
      if (objectToMap == null) {
        return null;
      }
      return new ObjectMapped(
        objectToMap.getStringParameter(),
        objectToMap.getIntegerParameter(),
        objectToMap.getDoubleParameter(),
        objectToMap.getObjectToMaps() == null
          ? Collections.emptyList()
          : objectToMap.getObjectToMaps().stream().map(MyMapper::map).collect(Collectors.toList()),
        MyMapper.map(objectToMap.getObjectToMap())
      );
    }
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  private static class ObjectToMap {
    private String stringParameter;
    private Integer integerParameter;
    private Double doubleParameter;
    private List<ObjectToMap> objectToMaps;
    private ObjectToMap objectToMap;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  private static class ObjectMapped {
    private String stringParameter;
    private Integer integerParameter;
    private Double doubleParameter;
    private List<ObjectMapped> objectMappeds;
    private ObjectMapped objectMapped;
  }
}
