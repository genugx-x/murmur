package com.genug.murmur.utils.logtrace;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TraceId {
    private String id;
    private int level;

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    @Builder
    public TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public TraceId createNextTraceId(TraceId traceId) {
        return TraceId.builder()
                .id(traceId.getId())
                .level(traceId.getLevel() + 1)
                .build();
    }

    public TraceId createPreviousTraceId(TraceId traceId) {
        return TraceId.builder()
                .id(traceId.getId())
                .level(traceId.getLevel() - 1)
                .build();
    }

    public boolean isFirstLevel() {
        return level == 0;
    }


}
