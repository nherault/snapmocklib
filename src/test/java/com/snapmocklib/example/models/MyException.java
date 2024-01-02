package com.snapmocklib.example.models;

public class MyException extends RuntimeException {
    // For Deserializing an exception, you MUST have at least one constructor with the message as single parameter
    public MyException(String message) {
        super(message);
    }
}
