package com.snapmocklib.services.customserialization;

import java.util.List;
import java.util.Optional;

public class OptionalCustomSerialization implements CustomSerialization {
  @Override
  public List<String> getReturnTypes(Object result) {
    var value = ((Optional<?>) result).orElse(null);
    return value == null
      ? List.of(Optional.class.getName())
      : List.of(Optional.class.getName(), value.getClass().getName());
  }

  @Override
  public Object recordValue(Object result) {
    return ((Optional<?>) result).orElse(null);
  }

  @Override
  public Object playbackValue(Object result) {
    return Optional.ofNullable(result);
  }
}
