package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.EnumTest;
import com.snapmocklib.example.models.MyException;

import java.util.UUID;

public class MyFirstOutputImpl implements MyFirstOutputSpi {

    @Override
    public EnumTest myFirstOutputMethod(UUID uuidParam, Integer integerParam) throws MyException {
        if (integerParam == 0) {
            return EnumTest.EXCEPTION;
        } else if (integerParam == 1) {
            return EnumTest.VALUE1;
        } else if (integerParam == 2) {
            return EnumTest.VALUE2;
        } else if (integerParam == 3) {
            return EnumTest.VALUE1;
        } else if (integerParam == 4) {
            throw new MyException("Exception from first output");
        }
        return null;
    }
}
