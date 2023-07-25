package com.genug.murmur.utils.logtrace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogTraceImpl implements LogTrace {

    private final static String BEGIN_PREFIX = "-->";
    private final static String END_COMPLETE_PREFIX = "<--";
    private final static String EXCEPTION_COMPLETE_PREFIX = "<x-";
    private final ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public LogTraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        long startTimeMillis = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(traceId.getLevel(), BEGIN_PREFIX), message);
        return LogTraceStatus.builder()
                .traceId(traceId)
                .message(message)
                .startTimeMillis(startTimeMillis)
                .build();
    }

    @Override
    public void end(LogTraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(LogTraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(LogTraceStatus status, Exception e) {
        long elapsedTimeMillis = System.currentTimeMillis() - status.getStartTimeMillis();
        TraceId traceId = traceIdHolder.get();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(traceId.getLevel(), END_COMPLETE_PREFIX), status.getMessage(), elapsedTimeMillis);
        } else {
            log.error("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(traceId.getLevel(), EXCEPTION_COMPLETE_PREFIX), status.getMessage(), elapsedTimeMillis, e.toString());
        }
        releaseTraceId();
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextTraceId(traceId));
        }
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();
        } else {
            traceIdHolder.set(traceId.createPreviousTraceId(traceId));
        }
    }

    private String addSpace(int level, String prefix) {
        String step = "|   ";
        return step.repeat(Math.max(0, level)) + "|" + prefix;
    }


}
