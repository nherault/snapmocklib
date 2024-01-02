package com.snapmocklib.services.aspect;

import com.snapmocklib.services.RecordService;
import com.snapmocklib.utils.AspectUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;

public class StepOutputAspectService extends RecordService {

    public Object callOutput(ProceedingJoinPoint pjp) throws Throwable {
        return this.callOutput(
                AspectUtils.getCaller(pjp),
                AspectUtils.getArgs(pjp),
                AspectUtils.getReturnType(pjp),
                pjp::proceed);
    }
    public Object callOutput(ProceedingJoinPoint pjp, List<String> fieldKeys) throws Throwable {
        return this.callOutput(
                AspectUtils.getCaller(pjp),
                AspectUtils.getArgs(pjp),
                AspectUtils.getReturnType(pjp),
                pjp::proceed,
                fieldKeys);
    }

    public Object callStep(ProceedingJoinPoint pjp) throws Throwable {
        return this.callStep(
                AspectUtils.getCaller(pjp),
                AspectUtils.getArgs(pjp),
                AspectUtils.getReturnType(pjp),
                pjp::proceed);
    }
}
