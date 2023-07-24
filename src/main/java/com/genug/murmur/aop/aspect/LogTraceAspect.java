package com.genug.murmur.aop.aspect;

import com.genug.murmur.utils.logtrace.LogTrace;
import com.genug.murmur.utils.logtrace.LogTraceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogTraceAspect {

    private final LogTrace logTrace;

    // api 패키지 하위의 모든 경로의 클래스와 메서드
    @Around("execution(* com.genug.murmur.api..*(..))") // pointcut
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
