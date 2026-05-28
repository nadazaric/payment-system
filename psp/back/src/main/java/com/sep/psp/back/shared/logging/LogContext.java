package com.sep.psp.back.shared.logging;

import org.slf4j.MDC;

public final class LogContext implements AutoCloseable {

    private LogContext(
            String feature,
            String action
    ) {
        MDC.put("feature", feature);
        MDC.put("action", action);
    }

    public static LogContext of(
            String feature,
            String action
    ) {
        return new LogContext(feature, action);
    }

    @Override
    public void close() {
        MDC.remove("feature");
        MDC.remove("action");
    }
}