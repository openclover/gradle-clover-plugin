package com.atlassian.gradle.plugin.clover.util

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import groovy.mock.interceptor.MockFor
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.junit.Test

class GradleCloverLoggerTest extends CloverBaseUTCase {

    @Test
    void testWhenKnownLogLevel() {
        def mockForLogger = new MockFor(Logger.class)
        mockForLogger.demand.log { logLevel, msg -> logLevel == LogLevel.INFO && msg == "Msg" }

        def mockLogger = mockForLogger.proxyInstance()
        def cloverLogger = new GradleCloverLogger(mockLogger)

        cloverLogger.info("Msg")

        mockForLogger.verify mockLogger
    }

    @Test
    void testWhenUnKnownLogLevel() {
        def mockForLogger = new MockFor(Logger.class)
        mockForLogger.demand.log { logLevel, msg -> logLevel == LogLevel.INFO && msg == "Msg" }

        def mockLogger = mockForLogger.proxyInstance()
        def cloverLogger = new GradleCloverLogger(mockLogger)

        cloverLogger.log(20, "Msg", null)

        mockForLogger.verify mockLogger
    }

    @Test
    void testWhenExceptionLogging() {
        def ex = new Exception()
        def mockForLogger = new MockFor(Logger.class)
        mockForLogger.demand.log { logLevel, msg, exception -> logLevel == LogLevel.ERROR && msg == "Msg" && exception == ex }

        def mockLogger = mockForLogger.proxyInstance()
        def cloverLogger = new GradleCloverLogger(mockLogger)


        cloverLogger.error("Msg", ex)

        mockForLogger.verify mockLogger
    }


}
