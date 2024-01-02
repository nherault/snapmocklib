package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.EnumTest;

import java.util.List;
import java.util.Optional;

public class NoSerializeTypesIntegrationService implements NoSerializeTypesIntegrationApi {

  private final NoSerializeTypesApi noSerializeTypesApi;

  public NoSerializeTypesIntegrationService(NoSerializeTypesApi noSerializeTypesApi) {
    this.noSerializeTypesApi = noSerializeTypesApi;
  }

  @Override
  public List<EnumTest> getEnums() {
    return noSerializeTypesApi.getEnums();
  }

  @Override
  public Optional<EnumTest> getEnumByName(String name) {
    return noSerializeTypesApi.getEnumByName(name);
  }
}
