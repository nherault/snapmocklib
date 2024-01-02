package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.MyException;
import com.snapmocklib.example.models.SecondOutputResult;

public class MySecondOutputImpl implements MySecondOutputSpi {
    @Override
    public SecondOutputResult mySecondOutputMethod(Integer integerParam) throws MyException {
        if (integerParam != null) {
            if (integerParam == 0) {
                return new SecondOutputResult(1);
            } else if (integerParam == 1) {
                return new SecondOutputResult(2);
            }
        }
        throw new MyException("Exception from second output");
    }
}
