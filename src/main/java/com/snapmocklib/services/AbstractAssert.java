package com.snapmocklib.services;

import com.snapmocklib.models.StepCaller;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@AllArgsConstructor
public abstract class AbstractAssert {

  protected final RecordService recordService;

  protected List<StepCaller> assertNoThrow(String caller) {
    var stepCallerRecorded = assertHasBeenCalled(caller);
    Assertions.assertFalse(
      stepCallerRecorded.stream().allMatch(StepCaller::getException),
      String.format("Should NOT be an exception: %s ", stepCallerRecorded));
    return stepCallerRecorded;
  }

  protected List<StepCaller> assertHasThrow(String caller, Class<? extends Exception> exception, String containsMessage) {
    var stepCallerRecorded = assertHasBeenCalled(caller);
    Assertions.assertTrue(
      stepCallerRecorded.stream().allMatch(StepCaller::getException),
      String.format("At least one StepCaller is not and Exception: %s", stepCallerRecorded));
    Assertions.assertTrue(
      stepCallerRecorded.stream().allMatch(s -> exception.getName().equals(s.getReturnType())),
      String.format("At least one StepCaller does not match the exception '%s': %s", exception.getName(), stepCallerRecorded));
    Assertions.assertTrue(
      stepCallerRecorded.stream().allMatch(s -> ((Exception) s.getResult()).getMessage().contains(containsMessage)),
      String.format("At least one StepCaller does not contain the exception message: %s", containsMessage)
    );
    return stepCallerRecorded;
  }

  protected void assertHasThrow(Supplier<?> supplier, Class<? extends Exception> exception, String containsMessage) {
    try {
      supplier.get();
    } catch (Exception e) {
        Assertions.assertEquals(e.getClass(), exception);
        Assertions.assertEquals(e.getMessage(), containsMessage);
    }
  }

  protected List<StepCaller> assertHasNotBeenCalled(String caller) {
    var stepCallerRecorded = recordService.getStepCaller(caller);
    Assertions.assertTrue(
      stepCallerRecorded.isEmpty(),
      String.format("The StepCaller has been called: %s", stepCallerRecorded)
      );
    return stepCallerRecorded;
  }

  protected List<StepCaller> assertHasBeenCalled(String caller) {
    var stepCallerRecorded = recordService.getStepCaller(caller);
    Assertions.assertFalse(
      stepCallerRecorded.isEmpty(),
      String.format("The StepCaller has not been called: %s", caller));
    return stepCallerRecorded;
  }

  protected List<StepCaller> assertHasBeenCalledWith(String caller, Map<String, Object> args) {
    var stepCallerRecorded = assertHasBeenCalled(caller);
    Assertions.assertFalse(stepCallerRecorded.stream()
      .map(StepCaller::getArgs)
      .allMatch(stringObjectMap -> stringObjectMap.equals(args)),
      String.format("At least one StepCaller does not match the arguments: %s", stepCallerRecorded));
    return stepCallerRecorded;
  }

  protected List<StepCaller> assertHasBeenCalledAndReturn(String caller, Object result) {
    var stepCallerRecorded = assertHasBeenCalled(caller);
    Assertions.assertFalse(stepCallerRecorded.stream()
      .map(StepCaller::getResult)
      .allMatch(o -> o.equals(result)),
      String.format("At least one StepCaller does not match the result: %s", result));
    return stepCallerRecorded;
  }

  public List<StepCaller> assertHasBeenCalledWithAndReturn(String caller, Map<String, Object> args, Object result) {
    var stepCallerRecorded = assertHasBeenCalled(caller);
    Assertions.assertFalse(stepCallerRecorded.stream()
      .map(StepCaller::getArgs)
      .allMatch(stringObjectMap -> stringObjectMap.equals(args)),
      String.format("At least one StepCaller does not match the arguments: %s", stepCallerRecorded));
    Assertions.assertFalse(stepCallerRecorded.stream()
      .map(StepCaller::getResult)
      .allMatch(o -> o.equals(result)),
      String.format("At least one StepCaller does not match the result: %s", stepCallerRecorded));
    return stepCallerRecorded;
  }
}
