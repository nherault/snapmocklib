package com.snapmocklib.example.unit;

import com.snapmocklib.example.classes.NoSerializeTypesApi;
import com.snapmocklib.example.classes.NoSerializeTypesService;
import com.snapmocklib.example.models.EnumTest;
import com.snapmocklib.example.models.MyException;
import com.snapmocklib.services.mock.AbstractSnapMockLibTest;
import org.junit.jupiter.api.Test;

class NoSerializableUnitTest extends AbstractSnapMockLibTest {

  private final NoSerializeTypesApi noSerializeTypesApi;

  NoSerializableUnitTest() {
    this.noSerializeTypesApi = mockStep(
      new NoSerializeTypesService()
    );
  }

  @Test
  void unitTestWithList() throws MyException {
    this.noSerializeTypesApi.getEnums();
    assertNoThrow("NoSerializeTypesApi.getEnums");
  }

  @Test
  void unitTestWithOptional() throws MyException {
    this.noSerializeTypesApi.getEnumByName(EnumTest.VALUE1.name());
    assertNoThrow("NoSerializeTypesApi.getEnumByName");
  }

  @Test
  void unitTestWithOptionalEmpty() throws MyException {
    this.noSerializeTypesApi.getEnumByName("not a value");
    assertNoThrow("NoSerializeTypesApi.getEnumByName");
  }
}
