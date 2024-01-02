package com.snapmocklib.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LogUtils {

    private static final String PREFIX = "[SNAP_MOCK_LIB]: ";

    private LogUtils() {}

    public static void info(String message) {
        log.info(PREFIX + message);
    }

    public static void warn(String message) {
        log.warn(PREFIX + message);
    }

    public static void error(String message) {
        log.error(PREFIX + message);
    }

    public static RuntimeException errorWithException(String message) {
        log.error(PREFIX + message);
        return new RuntimeException(message);
    }
}
