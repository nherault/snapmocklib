package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.MyException;
import com.snapmocklib.example.models.SecondOutputResult;

public interface MySecondOutputSpi {
    SecondOutputResult mySecondOutputMethod(Integer integerParam) throws MyException;
}
