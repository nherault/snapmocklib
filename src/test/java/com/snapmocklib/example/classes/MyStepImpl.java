package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.MyException;

import java.util.UUID;

public class MyStepImpl implements MyStepSpi {

    @Override
    public void myStepMethod(UUID uuidParam, Integer integerParam) {
        if (integerParam == -1) {
            throw new MyException("Exception occur during the step");
        }
    }
}
