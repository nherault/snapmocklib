package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.EnumTest;

import java.util.List;
import java.util.Optional;

public interface NoSerializeTypesApi {

  List<EnumTest> getEnums();

  Optional<EnumTest> getEnumByName(String name);
}
