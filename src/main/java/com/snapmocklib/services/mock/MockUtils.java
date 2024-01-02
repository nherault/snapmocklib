package com.snapmocklib.services.mock;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public final class MockUtils {

    private MockUtils() {
    }

    public static Map<String, Object> getArgs(Method method, Object[] args) {
        Map<String, Object> argMap = new HashMap<>();
        if (args != null) {
            Parameter[] parameter = method.getParameters();
            for (int i = 0; i < args.length; i++) {
                argMap.put(parameter[i].getName(), args[i]);
            }
        }
        return argMap;
    }

    public static String getCaller(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }
}
