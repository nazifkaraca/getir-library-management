package com.getir.library_management.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Define a pointcut that targets all methods within classes annotated with @RestController
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    // Log method invocation details before and after execution of controller methods
    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // Retrieve method signature details
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // Extract class and method names for logging
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        // Log incoming method call with arguments
        log.info("INCOMING  {}.{}() called with args: {}", className, methodName, Arrays.toString(joinPoint.getArgs()));

        // Proceed with method execution
        Object result = joinPoint.proceed();

        // Log returned result after method execution
        log.info("OUTGOING  {}.{}() returned: {}", className, methodName, result);

        return result;
    }
}
