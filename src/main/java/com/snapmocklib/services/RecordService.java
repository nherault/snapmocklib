package com.snapmocklib.services;

import com.snapmocklib.api.FileRecordPersistence;
import com.snapmocklib.api.JsonConverter;
import com.snapmocklib.api.JsonConverterPort;
import com.snapmocklib.api.RecordPersistencePort;
import com.snapmocklib.models.Mode;
import com.snapmocklib.models.StepCaller;
import com.snapmocklib.models.TestScenarioResult;
import com.snapmocklib.services.customserialization.CustomSerialization;
import com.snapmocklib.services.customserialization.OptionalCustomSerialization;
import com.snapmocklib.utils.JsonAssertUtils;
import com.snapmocklib.utils.JsonUtils;
import com.snapmocklib.utils.LogUtils;
import lombok.Getter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RecordService {

  private static final JsonAssertUtils JSON_ASSERT_UTILS = new JsonAssertUtils();
  private final RecordPersistencePort recordPersistence;

  @Getter
  private final JsonConverterPort jsonConverter;
  private TestScenarioResult testScenarioResult;

  private String snapshotReference;

  private Mode mode;

  private int callIndex;

  private Map<String, Pattern> fieldsToReplace;

  private static final Map<String, CustomSerialization> customSerializationMap = new HashMap<>();

  private final PlaybackService playbackService;

  static {
    customSerializationMap.put("java.util.Optional", new OptionalCustomSerialization());
  }

  public RecordService() {
    this(new JsonConverter(JsonUtils.mapper), new FileRecordPersistence());
  }

  public RecordService(JsonConverterPort jsonConverter, RecordPersistencePort recordPersistence) {
    this.jsonConverter = jsonConverter;
    this.recordPersistence = recordPersistence;
    this.playbackService = new PlaybackService(jsonConverter, customSerializationMap);
  }

  public void startTest(String testId, Mode mode, Map<String, Pattern> fieldsToReplace) {
    LogUtils.info("Start test [" + testId + "] with mode: [" + mode + "]");
    this.mode = mode;
    this.testScenarioResult = new TestScenarioResult();
    this.testScenarioResult.setTestId(testId);
    this.snapshotReference = this.getSnapshotReference(testId);
    this.callIndex = 0;
    this.fieldsToReplace = fieldsToReplace;
    this.playbackService.initTest(snapshotReference);
  }

  public boolean endTest() throws IOException {
    LogUtils.info("End test [" + testScenarioResult.getTestId() + "] with mode: [" + mode + "]");
    boolean isSnapshotRecorded = manageRecordPersistence();
    if (isSnapshotRecorded) {
      LogUtils.info("Snapshot reference just recorded, no need to assert...");
    } else {
      JSON_ASSERT_UTILS.assertJson(snapshotReference, jsonConverter.toJson(testScenarioResult), fieldsToReplace);
    }
    return isSnapshotRecorded;
  }

  public List<StepCaller> getStepCaller(String caller) {
    var stepCallers = testScenarioResult.getSteps().stream()
      .filter(stepCaller -> stepCaller.getCaller().contains(caller))
      .collect(Collectors.toList());
    stepCallers.addAll(testScenarioResult.getOutputs().stream()
      .filter(stepCaller -> stepCaller.getCaller().contains(caller))
      .collect(Collectors.toList()));
    return stepCallers;
  }

  public Object callOutput(String caller, Map<String, Object> args, Class<?> returnType, ThrowingSupplier<?> supplier) throws Throwable {
    return this.callOutput(caller, args, returnType, supplier, Collections.emptyList());
  }

  public Object callOutput(String caller, Map<String, Object> args, Class<?> returnType, ThrowingSupplier<?> supplier, List<String> fieldKeys) throws Throwable {
    if (mode == Mode.PLAYBACK) {
      return playbackOutput(caller, args, fieldKeys);
    } else if (mode == Mode.AUTO_PLAYBACK) {
      return this.snapshotReference == null
        ? recordOutput(caller, args, returnType, supplier)
        : playbackOutput(caller, args, fieldKeys);
    } else {
      return recordOutput(caller, args, returnType, supplier);
    }
  }

  public Object callStep(String caller, Map<String, Object> args, Class<?> returnType, ThrowingSupplier<?> supplier) throws Throwable {
    return recordStepCall(
      caller,
      args,
      returnType,
      supplier,
      stepCaller -> testScenarioResult.getSteps().add(stepCaller),
      true);
  }

  public RecordService addCustomSerialization(String returnType, CustomSerialization customSerialization) {
    customSerializationMap.put(returnType, customSerialization);
    return this;
  }

  public RecordService clearCustomSerialization() {
    customSerializationMap.clear();
    return this;
  }

  private Object playbackOutput(String caller, Map<String, Object> args, List<String> fieldKeys) throws Throwable {
    return this.playbackService.playbackOutput(
      caller,
      args,
      fieldKeys,
      stepCaller -> testScenarioResult.getOutputs().add(
        new StepCaller(
          ++callIndex,
          caller,
          args,
          stepCaller.getException(),
          stepCaller.getReturnType(),
          stepCaller.getResult()
        )
      ));
  }

  private Object recordOutput(String caller, Map<String, Object> args, Class<?> returnType, ThrowingSupplier<?> supplier) throws Throwable {
    return recordStepCall(
      caller,
      args,
      returnType,
      supplier,
      stepCaller -> testScenarioResult.getOutputs().add(stepCaller),
      false);
  }

  private Object recordStepCall(
    String caller,
    Map<String, Object> args,
    Class<?> returnType,
    ThrowingSupplier<?> supplier,
    Consumer<StepCaller> recordStepCaller,
    boolean isReplaceFields) throws Throwable {

    Object result;
    final int index = ++callIndex;
    try {
      result = supplier.get();
    } catch (Exception e) {
      recordStepCaller.accept(new StepCaller(
        index,
        caller,
        (Map<String, Object>) JsonUtils.deepCopy(args, fieldsToReplace, Map.class.getName()),
        true,
        e.getClass().getName(),
        overrideThrowable(e)
      ));
      throw e;
    }
    recordStepCaller.accept(new StepCaller(
      index,
      caller,
      (Map<String, Object>) JsonUtils.deepCopy(args, fieldsToReplace, Map.class.getName()),
      false,
      generateReturnType(returnType.getName(), result),
      JsonUtils.deepCopy(
        getValueToRecord(returnType.getName(), result),
        isReplaceFields ? fieldsToReplace : Map.of(),
        generateReturnType(returnType.getName(), result))
    ));
    return result;
  }

  private String generateReturnType(String returnType, Object result) {
    var customSerialization = customSerializationMap.get(returnType);
    return customSerialization == null
      ? returnType
      : String.join(";", customSerialization.getReturnTypes(result));
  }

  private Object getValueToRecord(String returnType, Object result) {
    var customSerialization = customSerializationMap.get(returnType);
    return customSerialization == null
      ? result
      : customSerialization.recordValue(result);
  }

  private boolean manageRecordPersistence() throws IOException {
    if (isGenerateRecord()) {
      LogUtils.info("Record for [" + testScenarioResult.getTestId() + "] generated");
      generateRecord();
      return true;
    } else {
      LogUtils.info("Record already exist for [" + testScenarioResult.getTestId() + "], nothing to generate");
      return false;
    }
  }

  private boolean isGenerateRecord() {
    return mode == Mode.RECORD || ((mode == Mode.AUTO || mode == Mode.AUTO_PLAYBACK) && this.snapshotReference == null);
  }

  private String getSnapshotReference(String testId) {
    try {
      String reference = recordPersistence.getRecord(testId);
      LogUtils.info("Snapshot reference found for test with id: " + testId);
      return reference;
    } catch (IOException e) {
      LogUtils.info("No snapshot reference for test with id: " + testId);
      return null;
    }
  }

  private void generateRecord() throws IOException {
    snapshotReference = jsonConverter.toJson(testScenarioResult);
    recordPersistence.saveRecord(
      testScenarioResult.getTestId(),
      this.snapshotReference
    );
  }

  // Override the throwable to avoid recording the "cause", which may be a cyclic reference
  private Throwable overrideThrowable(Throwable e) {
    return new Exception(e.getMessage());
  }
}
