package com.genug.murmur.aop.aspect;

import com.genug.murmur.utils.logtrace.LogTrace;
import com.genug.murmur.utils.logtrace.LogTraceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogTraceAspect {

    private final LogTrace logTrace;

    @Pointcut("execution(* com.genug.murmur.api.service..*(..))")
    public void service(){}

    @Pointcut("execution(* com.genug.murmur.api.controller..*(..))")
    public void controller(){}

//    @Pointcut("execution(* com.genug.murmur.security.JwtAuthenticationFilter.*(..))")
//    public void jwtAuthenticationFilter(){}

    @Around("service() || controller()") // pointcut
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        // advice
        LogTraceStatus status = null;
        String message = joinPoint.getSignature().toShortString();
        try {
            status = logTrace.begin(message);
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }


}
