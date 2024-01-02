package com.snapmocklib.example.classes;

import com.snapmocklib.example.models.EnumTest;
import com.snapmocklib.example.models.MyException;

import java.util.UUID;

public interface MyFirstOutputSpi {
    EnumTest myFirstOutputMethod(UUID uuidParam, Integer integerParam) throws MyException;
}
