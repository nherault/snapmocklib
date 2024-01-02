package com.snapmocklib.services.mock;

import com.snapmocklib.services.RecordService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MockStepProxy implements java.lang.reflect.InvocationHandler {

    private final Object obj;

    private final RecordService recordService;

    public static Object newInstance(RecordService recordService, Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new MockStepProxy(recordService, obj));
    }

    private MockStepProxy(RecordService recordService, Object obj) {
        this.recordService = recordService;
        this.obj = obj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.setAccessible(true);
        return recordService.callStep(
                MockUtils.getCaller(method),
                MockUtils.getArgs(method,args),
                method.getReturnType(),
                () -> {
                    try {
                        return method.invoke(obj, args);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }
}
