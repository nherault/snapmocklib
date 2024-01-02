package com.snapmocklib.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartialResult {
    private String uuidParam;
    private String dateParam;
    private Integer integerParam;
}
