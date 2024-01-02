package com.snapmocklib.example.integration.config;

import com.snapmocklib.services.aspect.StepOutputAspectService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class IntegrationTestAspect {

    private final StepOutputAspectService stepOutputAspectService;

    public IntegrationTestAspect(
            StepOutputAspectService stepOutputAspectService
    ) {
        this.stepOutputAspectService = stepOutputAspectService;
    }

    @Around("execution(* com.snapmocklib.example.classes.MyApi.methodToTest(..))")
    public Object methodToTestCall(ProceedingJoinPoint pjp) throws Throwable {
        return this.stepOutputAspectService.callStep(pjp);
    }

    @Around("execution(* com.snapmocklib.example.classes.MyFirstOutputSpi.myFirstOutputMethod(..))")
    public Object myFirstOutputMethodCall(ProceedingJoinPoint pjp) throws Throwable {
        return this.stepOutputAspectService.callOutput(pjp, List.of("integerParam"));
    }

    @Around("execution(* com.snapmocklib.example.classes.MySecondOutputSpi.mySecondOutputMethod(..))")
    public Object mySecondOutputMethodCall(ProceedingJoinPoint pjp) throws Throwable {
        return this.stepOutputAspectService.callOutput(pjp, List.of("integerParam"));
    }

    @Around("execution(* com.snapmocklib.example.classes.MyThirdOutputSpi.myThirdOutputMethod(..))")
    public Object myThirdOutputMethodCall(ProceedingJoinPoint pjp) throws Throwable {
        return this.stepOutputAspectService.callOutput(pjp);
    }

    @Around("execution(* com.snapmocklib.example.classes.MyStepSpi.myStepMethod(..))")
    public Object myStepMethodCall(ProceedingJoinPoint pjp) throws Throwable {
        return this.stepOutputAspectService.callStep(pjp);
    }

    @Around("execution(* com.snapmocklib.example.classes.NoSerializeTypesIntegrationApi.*(..))")
    public Object noSerializeTypesIntegrationApiStepMethodCall(ProceedingJoinPoint pjp) throws Throwable {
        return this.stepOutputAspectService.callStep(pjp);
    }

    @Around("execution(* com.snapmocklib.example.classes.NoSerializeTypesApi.*(..))")
    public Object noSerializeTypesApiOutputMethodCall(ProceedingJoinPoint pjp) throws Throwable {
        return this.stepOutputAspectService.callOutput(pjp);
    }
}
