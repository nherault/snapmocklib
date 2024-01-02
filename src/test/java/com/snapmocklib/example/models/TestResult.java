package com.snapmocklib.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestResult {
    private List<PartialResult> partialResults;
    private Set<SecondOutputResult> secondOutputResults;
}
