package com.genug.murmur.utils.logtrace;

public interface LogTrace {
    LogTraceStatus begin(String message);
    void end(LogTraceStatus status);
    void exception(LogTraceStatus status, Exception e);
}
