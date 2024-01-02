package com.snapmocklib.services.mock;

import com.snapmocklib.services.RecordService;
import com.snapmocklib.utils.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class MockStatic {

    private final RecordService snapMockLibRecordService;

    private final Class<?> clazz;

    public static MockStatic newInstance(RecordService snapMockLibRecordService, Class<?> clazz) {
        return new MockStatic(snapMockLibRecordService, clazz);
    }

    private MockStatic(RecordService snapMockLibRecordService, Class<?> clazz) {
        this.snapMockLibRecordService = snapMockLibRecordService;
        this.clazz = clazz;
    }

    public Object invokeStatic(String methodName, Object... args) throws Throwable {
        Method method = findMethod(clazz, methodName, args);
        method.setAccessible(true);
        return snapMockLibRecordService.callStep(
            MockUtils.getCaller(method),
            MockUtils.getArgs(method,args),
            method.getReturnType(),
            () -> {
                try {
                    return method.invoke(null, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw LogUtils.errorWithException(e.getMessage());
                }
            });
    }

    private Method findMethod(Class<?> clazz, String methodName, Object... args) throws NoSuchMethodException {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Parameter[] parameters = method.getParameters();
                if (args.length == parameters.length) {
                    for (int j = 0; j < parameters.length; j++) {
                        if (parameters[j].getClass() != args[j].getClass()) {
                            break;
                        }
                    }
                    return method;
                }
            }
        }
        throw new NoSuchMethodException();
    }
}
