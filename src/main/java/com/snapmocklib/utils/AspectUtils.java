package com.snapmocklib.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

public final class AspectUtils {

    private AspectUtils() {
    }

    public static Class<?> getReturnType(ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getReturnType();
    }

    public static String getCaller(ProceedingJoinPoint pjp) {
        return pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
    }
    public static Map<String, Object> getArgs(ProceedingJoinPoint pjp) {
        Map<String, Object> args = new HashMap<>();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        for (int i = 0; i < signature.getParameterNames().length; i++) {
            args.put(
                    signature.getParameterNames()[i],
                    pjp.getArgs()[i]
            );
        }
        return args;
    }
}
