package com.genug.murmur.utils.logtrace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogTraceImpl implements LogTrace {

    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public LogTraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        long startTimeMillis = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(traceId.getLevel(), "-->"), message);
        return LogTraceStatus.builder()
                .traceId(traceId)
                .message(message)
                .startTimeMillis(startTimeMillis)
                .build();
    }

    @Override
    public void end(LogTraceStatus status) {
        TraceId traceId = traceIdHolder.get();
        long elapsedTimeMillis = System.currentTimeMillis() - status.getStartTimeMillis();
        log.info("[{}] {}{} [{}ms]", status.getTraceId().getId(), addSpace(traceId.getLevel(), "<--"), status.getMessage(), elapsedTimeMillis);
        releaseTraceId();
    }

    @Override
    public void exception(LogTraceStatus status, Exception e) {
        TraceId traceId = traceIdHolder.get();
        long elapsedTimeMillis = System.currentTimeMillis() - status.getStartTimeMillis();
        log.info("[{}] {}{} time={}ms ex={}", status.getTraceId().getId(), addSpace(traceId.getLevel(), "<x-"), status.getMessage(), elapsedTimeMillis, e.toString());
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
        StringBuilder sb = new StringBuilder();
        String step = "|   ";
        sb.append(step.repeat(Math.max(0, level)))
                .append("|")
                .append(prefix);
        return sb.toString();
    }


}
