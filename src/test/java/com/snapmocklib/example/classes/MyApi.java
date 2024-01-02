package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.MyException;
import com.snapmocklib.example.models.TestResult;

public interface MyApi {
    TestResult methodToTest(Integer integerParam) throws MyException;
}
