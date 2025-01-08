package org.example.echo01.common.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* org.example.echo01..*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.debug("Entering method {} in class {}", methodName, className);
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.debug("Method {} in class {} completed in {}ms", methodName, className, (endTime - startTime));
            return result;
        } catch (Exception e) {
            log.error("Error in method {} in class {}: {}", methodName, className, e.getMessage(), e);
            throw e;
        }
    }
} 