package com.snapmocklib.services.mock;

import com.snapmocklib.api.JsonConverterPort;
import com.snapmocklib.services.RecordService;
import com.snapmocklib.utils.LogUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public final class MockOutputProxy implements java.lang.reflect.InvocationHandler {

    private final RecordService recordService;

    private final List<AbstractSnapMockLibTest.MockMethod> mockReturns;

    public static Object newInstance(
      RecordService recordService,
            Class<?> clazz,
      List<AbstractSnapMockLibTest.MockMethod> mockReturns) {
        return java.lang.reflect.Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[] { clazz },
                new MockOutputProxy(recordService, mockReturns));
    }

    private MockOutputProxy(RecordService recordService, List<AbstractSnapMockLibTest.MockMethod> mockReturns) {
        this.recordService = recordService;
        this.mockReturns = mockReturns;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        method.setAccessible(true);
        Object returnValue = getMockValue(method, args);
        if (returnValue instanceof Throwable) {
            return recordService.callOutput(
                    MockUtils.getCaller(method),
                    MockUtils.getArgs(method,args),
                    method.getReturnType(),
                    () -> {
                        throw (Throwable) returnValue;
                    });
        } else if (returnValue == null || method.getReturnType().isInstance(returnValue)) {
            return recordService.callOutput(
                    MockUtils.getCaller(method),
                    MockUtils.getArgs(method,args),
                    method.getReturnType(),
                    () -> returnValue);
        } else {
            LogUtils.error("Output mock return value for " + method.getName() + " not in the actual instance: " + method.getReturnType().getName());
            return null;
        }
    }

    private Object getMockValue(Method method, Object[] args) {
        String mockId = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        Optional<AbstractSnapMockLibTest.MockMethod> optionalMockMethod = mockReturns.stream()
                .filter(mockMethod -> mockId.equals(mockMethod.getMethod()))
                .filter(mockMethod -> {
                    if (mockMethod.getArgs() == null) {
                        return true;
                    }
                    JsonConverterPort jsonConverterPort = recordService.getJsonConverter();
                    return jsonConverterPort.toJson(args).equals(jsonConverterPort.toJson(mockMethod.getArgs()));
                })
                .findFirst();
        if (optionalMockMethod.isPresent()) {
            AbstractSnapMockLibTest.MockMethod mockMethod = optionalMockMethod.get();
            mockMethod.setCalled();
            return mockMethod.getResult();
        }
        if (!"void".equals(method.getReturnType().getName())) {
            throw LogUtils.errorWithException("Mock called without WHEN: " + method.getDeclaringClass().getName() + "." + method.getName());
        }
        return null;
    }
}
