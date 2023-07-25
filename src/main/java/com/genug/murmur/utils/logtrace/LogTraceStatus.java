package com.genug.murmur.utils.logtrace;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LogTraceStatus {
    private final TraceId traceId;
    private final String message;
    private final long startTimeMillis;

    @Builder
    public LogTraceStatus(TraceId traceId, String message, long startTimeMillis) {
        this.traceId = traceId;
        this.message = message;
        this.startTimeMillis = startTimeMillis;
    }
}
