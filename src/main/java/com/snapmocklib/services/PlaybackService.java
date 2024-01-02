package com.snapmocklib.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.snapmocklib.api.JsonConverterPort;
import com.snapmocklib.models.StepCaller;
import com.snapmocklib.models.TestScenarioResult;
import com.snapmocklib.services.customserialization.CustomSerialization;
import com.snapmocklib.utils.LogUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlaybackService {

  private final JsonConverterPort jsonConverter;

  private final Map<String, CustomSerialization> customSerializationMap;

  private List<StepCaller> outputs;

  private String snapshotReference;

  public PlaybackService(JsonConverterPort jsonConverter, Map<String, CustomSerialization> customSerializationMap) {
    this.jsonConverter = jsonConverter;
    this.customSerializationMap = customSerializationMap;
  }

  public void initTest(String snapshotReference) {
    this.outputs = null;
    this.snapshotReference = snapshotReference;
  }

  public Object playbackOutput(
    String caller,
    Map<String, Object> args,
    List<String> fieldKeys,
    Consumer<StepCaller> recordStepCaller) throws Throwable {
    StepCaller stepCaller = getStepOutputRecordedByCaller(caller, args, fieldKeys);

    recordStepCaller.accept(stepCaller);

    if (Boolean.TRUE.equals(stepCaller.getException())) {
      throw (Throwable) getResultFromException(stepCaller);
    } else {
      return getReturnValue(stepCaller);
    }
  }

  private Object getReturnValue(StepCaller stepCaller) throws Exception {
    try {
      if ("void".equals(stepCaller.getReturnType())) {
        return null;
      } else {
        return getValueToReturn(stepCaller);
      }
    } catch (Exception e) {
      LogUtils.error("[ERROR] While deserializing the result for step: " + stepCaller.getCaller() + ", exception: " + e.getMessage());
      throw e;
    }
  }

  private Object getValueToReturn(StepCaller stepCaller) throws Exception {
    var returnTypes = stepCaller.getReturnType().split(";");
    var customSerialization = customSerializationMap.get(returnTypes[0]);
    if (customSerialization == null) {
      return stepCaller.getResult() == null
        ? null
        : jsonConverter.fromJson(jsonConverter.toJson(stepCaller.getResult()), stepCaller.getReturnType());
    } else {
      if (stepCaller.getResult() == null) {
        return customSerialization.playbackValue(null);
      }
      var value = returnTypes.length > 1
        ? jsonConverter.fromJson(jsonConverter.toJson(stepCaller.getResult()), returnTypes[1])
        : jsonConverter.toJson(stepCaller.getResult());
      return customSerialization.playbackValue(value);
    }
  }

  private Object getResultFromException(StepCaller stepCaller) throws Exception {
    Object result = jsonConverter.fromJson(jsonConverter.toJson(stepCaller.getResult()), "java.lang.RuntimeException");
    return Arrays.stream(Class.forName(stepCaller.getReturnType()).getDeclaredConstructors())
      .filter(constructor -> constructor.getParameters().length == 1 && constructor.getParameterTypes()[0].isAssignableFrom(String.class))
      .findFirst()
      .orElseThrow(() -> LogUtils.errorWithException("For Deserializing an exception, you MUST have at least one constructor with the message as single parameter"))
      .newInstance(((RuntimeException) result).getMessage());
  }

  private StepCaller getStepOutputRecordedByCaller(String caller, Map<String, Object> args, List<String> fieldKeys) throws JsonProcessingException {
    if (this.outputs == null) {
      TestScenarioResult testScenarioResultRecorded = jsonConverter.fromJson(this.snapshotReference, TestScenarioResult.class);
      this.outputs = testScenarioResultRecorded.getOutputs();
    }
    int indexFound = -1;
    for (int i = 0; i < this.outputs.size(); i++) {
      if (isCorrectOutput(this.outputs.get(i), caller, args, fieldKeys)) {
        indexFound = i;
      }
    }
    if (indexFound == -1) {
      String error = "[ERROR] No output record found in playback mode with: [caller=" +
        caller +
        ", fieldKeys=" + String.join(",", fieldKeys) + "]";
      throw LogUtils.errorWithException(error);
    }
    return this.outputs.remove(indexFound);
  }

  private boolean isCorrectOutput(StepCaller stepCaller, String caller, Map<String, Object> args, List<String> keys) {
    if (stepCaller.getCaller().equals(caller)) {
      if (keys.isEmpty()) {
        return true;
      } else {
        String stepArgKey = stepCaller.getArgs().entrySet().stream()
          .filter(stringObjectEntry -> keys.contains(stringObjectEntry.getKey()))
          .map(stringObjectEntry -> jsonConverter.toJson(stringObjectEntry.getValue()))
          .collect(Collectors.joining("_"));
        String argKey = args.entrySet().stream()
          .filter(stringObjectEntry -> keys.contains(stringObjectEntry.getKey()))
          .map(stringObjectEntry -> jsonConverter.toJson(stringObjectEntry.getValue()))
          .collect(Collectors.joining("_"));
        return stepArgKey.equals(argKey);
      }
    }
    return false;
  }
}
