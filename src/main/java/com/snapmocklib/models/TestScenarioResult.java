package com.snapmocklib.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class TestScenarioResult {

    @Getter
    @Setter
    private String testId;

    @Getter
    private final List<StepCaller> outputs = new ArrayList<>();

    @Getter
    private final List<StepCaller> steps = new ArrayList<>();
}

