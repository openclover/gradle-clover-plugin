package com.atlassian.gradle.plugin.clover.config

import org.gradle.util.ConfigureUtil

class CloverPluginExtension {
    /**
     * Flag to determine if Clover should apply all necessary tasks and modifications
     * to let it run with minimal configuration scope. Automatic integration might not
     * work in heavily customized configurations
     */
    boolean automaticIntegration = true
    /**
     * Flag to determine if Clover plugin should be enabled. All Clover tasks depends on this flag
     */
    boolean enabled = Boolean.parseBoolean(System.getProperty("clover.enabled", "true"))
    String licenseLocation
    String license

    List<String> instrumentSourceSets = ["main"]

    File cloverDatabase

    /**
     * Configures whether Clover plugin should instrument test sources
     */
    boolean includesTestSourceRoots = true

    int flushInterval = -1
    FlushPolicy flushPolicy = FlushPolicy.DIRECTED
    InstrumentationLevel instrumentation = InstrumentationLevel.STATEMENT
    String jdk
    String encoding
    InstrumentLambdaLevel instrumentLambda = InstrumentLambdaLevel.ALL_BUT_REFERENCE
    boolean debug = false
    boolean useFullyQualifiedJavaLang = true

    CloverReportExtension report = new CloverReportExtension()

    def report(Closure closure) {
        ConfigureUtil.configure(closure, report)
    }

}
