package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.EnumTest;

import java.util.List;
import java.util.Optional;

public class NoSerializeTypesService implements NoSerializeTypesApi {
  @Override
  public List<EnumTest> getEnums() {
    return List.of(EnumTest.VALUE1, EnumTest.VALUE2);
  }

  @Override
  public Optional<EnumTest> getEnumByName(String name) {
    try {
      return Optional.of(EnumTest.valueOf(name));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
