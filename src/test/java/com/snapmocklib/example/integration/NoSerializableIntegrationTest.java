package com.snapmocklib.example.integration;

import com.snapmocklib.example.classes.NoSerializeTypesIntegrationApi;
import com.snapmocklib.example.models.EnumTest;
import com.snapmocklib.example.models.MyException;
import com.snapmocklib.services.RecordService;
import com.snapmocklib.services.integration.AbstractSnapMockLibIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NoSerializableIntegrationTest extends AbstractSnapMockLibIntegrationTest {

  private final NoSerializeTypesIntegrationApi noSerializeTypesIntegrationApi;

  @Autowired
  public NoSerializableIntegrationTest(RecordService recordService, NoSerializeTypesIntegrationApi noSerializeTypesIntegrationApi) {
    super(recordService);
    this.noSerializeTypesIntegrationApi = noSerializeTypesIntegrationApi;
  }

  @Test
  void integrationTestWithList() throws MyException {
    this.noSerializeTypesIntegrationApi.getEnums();
    assertNoThrow("NoSerializeTypesService.getEnums");
  }

  @Test
  void integrationTestWithOptional() throws MyException {
    this.noSerializeTypesIntegrationApi.getEnumByName(EnumTest.VALUE1.name());
    assertNoThrow("NoSerializeTypesService.getEnumByName");
  }

  @Test
  void integrationTestWithOptionalEmpty() throws MyException {
    this.noSerializeTypesIntegrationApi.getEnumByName("not a value");
    assertNoThrow("NoSerializeTypesService.getEnumByName");
  }
}
