package com.example.common.utils;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

public class TraceIdUtils {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String PARENT_SPAN_ID_KEY = "parentSpanId";

    public static String generateTraceId() {
        return IdUtil.fastSimpleUUID();
    }

    public static String generateSpanId() {
        return IdUtil.fastSimpleUUID().substring(0, 16);
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void setSpanId(String spanId) {
        MDC.put(SPAN_ID_KEY, spanId);
    }

    public static void setParentSpanId(String parentSpanId) {
        if (parentSpanId != null && !parentSpanId.isEmpty()) {
            MDC.put(PARENT_SPAN_ID_KEY, parentSpanId);
        }
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static String getSpanId() {
        return MDC.get(SPAN_ID_KEY);
    }

    public static String getParentSpanId() {
        return MDC.get(PARENT_SPAN_ID_KEY);
    }

    public static void removeTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    public static void removeSpanId() {
        MDC.remove(SPAN_ID_KEY);
    }

    public static void removeParentSpanId() {
        MDC.remove(PARENT_SPAN_ID_KEY);
    }

    public static void clear() {
        removeTraceId();
        removeSpanId();
        removeParentSpanId();
    }

    public static void setOrGenerateTraceId(String traceId) {
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }
        setTraceId(traceId);
    }

    public static void startSpan(String spanId, String parentSpanId) {
        if (spanId == null || spanId.isEmpty()) {
            spanId = generateSpanId();
        }
        setSpanId(spanId);
        setParentSpanId(parentSpanId);
    }

    public static void startNewSpan() {
        String oldSpanId = getSpanId();
        startSpan(generateSpanId(), oldSpanId);
    }
}
