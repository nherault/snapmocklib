package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.EnumTest;
import com.snapmocklib.example.models.MyException;
import com.snapmocklib.example.models.PartialResult;
import com.snapmocklib.example.models.SecondOutputResult;
import com.snapmocklib.example.models.TestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MyApiService implements MyApi {

    private final MyFirstOutputSpi myFirstOutputSpi;
    private final MySecondOutputSpi mySecondOutputSpi;
    private final MyThirdOutputSpi myThirdOutputSpi;
    private final MyStepSpi myStepSpi;

    public MyApiService(MyFirstOutputSpi myFirstOutputSpi,
                        MySecondOutputSpi mySecondOutputSpi,
                        MyThirdOutputSpi myThirdOutputSpi,
                        MyStepSpi myStepSpi) {
        this.myFirstOutputSpi = myFirstOutputSpi;
        this.mySecondOutputSpi = mySecondOutputSpi;
        this.myThirdOutputSpi = myThirdOutputSpi;
        this.myStepSpi = myStepSpi;
    }

    public TestResult methodToTest(Integer integerParam) throws MyException {
        UUID uuid = UUID.randomUUID();
        this.myStepSpi.myStepMethod(uuid, integerParam);
        EnumTest value = this.myFirstOutputSpi.myFirstOutputMethod(uuid, integerParam);
        List<PartialResult> partialResults = getPartialResults(value, uuid);

        Set<SecondOutputResult> secondOutputResults = new HashSet<>();
        if (partialResults.isEmpty()) {
            this.mySecondOutputSpi.mySecondOutputMethod(null);
        } else {
            for (PartialResult partialResult : partialResults) {
                secondOutputResults.add(this.mySecondOutputSpi.mySecondOutputMethod(partialResult.getIntegerParam()));
            }
        }

        this.myThirdOutputSpi.myThirdOutputMethod();

        return new TestResult(partialResults, secondOutputResults);
    }

    private List<PartialResult> getPartialResults(EnumTest value, UUID uuid) throws MyException {
        String date = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
        if (value == EnumTest.VALUE1) {
            return Collections.singletonList(new PartialResult(uuid.toString(), date, 0));
        } else if (value == EnumTest.VALUE2) {
            return List.of(
                    new PartialResult(uuid.toString(), date, 0),
                    new PartialResult(uuid.toString(), date, 1)
            );
        } else if (value == EnumTest.EXCEPTION) {
            throw new MyException("Exception occur during the test");
        } else {
            return Collections.emptyList();
        }
    }
}