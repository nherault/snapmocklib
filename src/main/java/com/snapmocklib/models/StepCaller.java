package com.snapmocklib.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StepCaller {

    private int index;
    private String caller;
    private Map<String, Object> args;
    private Boolean exception;
    private String returnType;
    private Object result;
}
