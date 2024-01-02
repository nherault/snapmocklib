# SNAPMOCK LIB

The goal of this library is to help doing the unit and integration test
by recording a snapshot of the scenario to test:

- Easy to create test
- Easy to maintain test
- Easy to figure out why the test failed

## How it works

**The best way is to check the test, to have an overall view of the possibility of the library.**

Example of scenario reference (in JSON):
```json
{
  "outputs" : [ {
    "args" : {
      "integerParam" : 1,
      "uuidParam" : null
    },
    "caller" : "com.snapmocklib.example.classes.MyFirstOutputImpl.myFirstOutputMethod",
    "exception" : false,
    "index" : 2,
    "result" : "VALUE1",
    "returnType" : "com.snapmocklib.example.models.EnumTest"
  }, {
    "args" : {
      "integerParam" : 0
    },
    "caller" : "com.snapmocklib.example.classes.MySecondOutputImpl.mySecondOutputMethod",
    "exception" : false,
    "index" : 3,
    "result" : {
      "integerParam" : 1
    },
    "returnType" : "com.snapmocklib.example.models.SecondOutputResult"
  }, {
    "args" : { },
    "caller" : "com.snapmocklib.example.classes.MyThirdOutputImpl.myThirdOutputMethod",
    "exception" : false,
    "index" : 4,
    "result" : null,
    "returnType" : "void"
  } ],
  "steps" : [ {
    "args" : {
      "integerParam" : 1
    },
    "caller" : "com.snapmocklib.example.classes.MyApiService.methodToTest",
    "exception" : false,
    "index" : 0,
    "result" : {
      "partialResults" : [ {
        "dateParam" : null,
        "integerParam" : 0,
        "uuidParam" : null
      } ],
      "secondOutputResults" : [ {
        "integerParam" : 1
      } ]
    },
    "returnType" : "com.snapmocklib.example.models.TestResult"
  }, {
    "args" : {
      "integerParam" : 1,
      "uuidParam" : null
    },
    "caller" : "com.snapmocklib.example.classes.MyStepImpl.myStepMethod",
    "exception" : false,
    "index" : 1,
    "result" : null,
    "returnType" : "void"
  } ],
  "testId" : "IntegrationTest#integrationTestWithValue1"
}
```

### Modes

There are several mode available:
- RECORD: Record the test and save the reference (the test always SUCCESS on RECORD mode)
- PLAYBACK: Record the test, return the result for **Outputs**, and assert the result with the reference
- CHECK: Record the test, call the outputs and assert the result with the reference
- AUTO: Automatically choose RECORD mode if no reference for the test exist, or CHECK otherwise
- AUTO_PLAYBACK: Automatically choose RECORD mode if no reference for the test exist, or PLAYBACK otherwise

On RECORD mode:
- Record the steps (payload and result)
- Record the outputs (payload and result)
- Save the result of the scenario (steps, outputs) with a **testId** as a reference

**/!\ The test automatically SUCCESS on RECORD mode**

On PLAYBACK mode:
- Record the steps (payload and result)
- Record the outputs payload (and retrieve the output result)
- Assert with the scenario reference and failed if something changes

## Unit test

For unit testing, you can extends the **AbstractSnapMockLibTest** witch automatically:
- Start each  test in **AUTO** mode with the name of the test as **testId**
- Clear the mocks before each test
- End the test by creating the scenario reference snapshot or assert the result with the reference (**AUTO** mode)

There are several rules to make it work correctly:
- ALL mock MUST an interface (Class to Test, and output dependencies)
- All Interface and Class must be public (if not the parameter names will be "arg0, arg1" instead of the real name)

See the example in the test (including static methods).

## Integration test with AOP

For integration testing, you can extends the **AbstractSnapMockLibIntegrationTest** witch automatically:
- Start each test in **AUTO_PLAYBACK** mode with the name of the test as **testId**
- End the test by creating the scenario reference snapshot or assert the result with the reference (**AUTO_PLAYBACK** mode)

You can use the **StepOutputAspectService**, if you want to use Aspect for registering **steps** and **outputs**.

See the example in the test.

## Using the library core

To use the library on a test, there are different steps:

### 1°) Start the test
```java
RecordService recordService = new RecordService();
recordService.startTest(String testId, Mode mode, Map<String, ReplaceData> fieldsToReplace);
```
With:
- **testId**: the identifier of the test
- **mode**: the mode of the test (RECORD, PLAYBACK, CHECK, AUTO, AUTO_PLAYBACK)
- **fieldsToReplace**: list of fields names to replace, if their value match the given pattern (for dynamic fields) and optionally the value to replace (**null** by default)

### 2°) Register the steps and outputs

**For steps:**
```java
RecordService recordService = new RecordService();
recordService.callStep(String caller, Map<String, Arg> args, Class<?> returnType, ThrowingSupplier<?> supplier);
```
With:
- **caller**: the method payload and result to register
- **args**: the list of the arguments by name with type and value
- **returnType**: the return type of the method
- **supplier**: the method to call

**For output:**
```java
RecordService recordService = new RecordService();
recordService.callOutput(String caller, Map<String, Arg> args, Class<?> returnType, ThrowingSupplier<?> supplier, List<String> fieldKeys);
// or: recordService.callOutput(String caller, Map<String, Arg> args, Class<?> returnType, ThrowingSupplier<?> supplier);
```
With:
- **caller**: the method payload and result to register
- **args**: the list of the arguments by name with type and value
- **returnType**: the return type of the method
- **supplier**: the method to call
- **fieldKeys** (optional): the fields to retrieve the right output (by caller) in playback mode

### 3°) End the test

```java
RecordService recordService = new RecordService();
recordService.endTest()
```

It will automatically save the scenario, depending of the **mode** you choosed.
And do the assertion with the snapshot reference.

4°) Extra assertions

You can use this method to get specific parts of the scenario by caller name:
```java
RecordService recordService = new RecordService();
// Return all the steps or outputs which 'caller' name contains the 'callerPartial' argument
List<StepCaller> isException = recordService.getStepCaller(callerPartial);
```

It is used to verify if specific call has been done, and assert the arguments and return value.

## Implementations

**RecordService** can take 2 parameters:
- **JsonConverterPort**: To convert the scenario to/from JSON, default implementation is using Jackson.
- **RecordPersistencePort**: To save the scenario reference, default implementation is using the filesystem
  - **#**: separator for the subdirectories (to categories tests, for example)
  - **scenarios**: Default directory to save the snapshot: **src/test/resources/scenarios**

## Limitations

- The library DO NOT support multithreading, so the tests cannot be start in parallel,
- For Deserializing an exception in **PLAYBACK mode**, you MUST have at least one constructor with the message as single parameter,
- The recorded exceptions are cast to 'Exception', to remove the 'cause' part, and avoid stackoverflow
- CustomSerialization is one layer deep, not recursive
- For Unit Test, the WHEN mock must take:
  - NO parameter (any parameters values will trigger the mock),
  - or you have to specify ALL parameters values