package com.example.common.utils;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

public class TraceIdUtils {

    private static final String TRACE_ID_KEY = "traceId";

    public static String generateTraceId() {
        return IdUtil.fastSimpleUUID();
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static void removeTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    public static void setOrGenerateTraceId(String traceId) {
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }
        setTraceId(traceId);
    }
}
