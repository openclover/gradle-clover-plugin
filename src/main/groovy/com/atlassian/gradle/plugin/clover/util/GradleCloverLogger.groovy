package com.atlassian.gradle.plugin.clover.util

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

class GradleCloverLogger extends com.atlassian.clover.Logger {
    private static final Map<Integer, LogLevel> loggerLevelMap = [
            0: LogLevel.ERROR,
            1: LogLevel.WARN,
            2: LogLevel.INFO,
            3: LogLevel.DEBUG,
            4: LogLevel.DEBUG
    ]

    private final Logger logger

    GradleCloverLogger(Logger logger) {
        this.logger = logger
    }

    @Override
    void log(int logLevel, String message, Throwable throwable) {
        LogLevel level = loggerLevelMap.get(logLevel) ?: LogLevel.INFO
        if (throwable != null) {
            logger.log(level, message, throwable)
        } else {
            logger.log(level, message)
        }
    }
}
