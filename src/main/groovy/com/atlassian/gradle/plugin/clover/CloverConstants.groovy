package com.atlassian.gradle.plugin.clover

interface CloverConstants {
    static final String CLOVER_EXTENSION = "clover"
    static final String CLOVER_SOURCE_SETS = "clover"
    static final String CLOVER_TEST_SOURCE_SETS = "clover-test"
    static final String DEFAULT_INSTR_DIR = "clover-instr"
    static final String DEFAULT_TEST_INSTR_DIR = "clover-instr-test"

    static final String CLOVER_COMPILE_CONFIGURATION_NAME = "cloverCompile"
    static final String CLOVER_TEST_COMPILE_CONFIGURATION_NAME = "cloverTestCompile"
    static final String CLOVER_RUNTIME_CONFIGURATION_NAME = "cloverRuntime"
    static final String CLOVER_TEST_RUNTIME_CONFIGURATION_NAME = "cloverTestRuntime"
    static final String CLOVER_CONFIGURATION_NAME = "cloverConfig"

    static final String CLOVER_COMPILE_CFG_DESC = "Clover compile time configuration"
    static final String CLOVER_CFG_DESC = "Clover Configuration configuration containing clover dependency and instrumented sources"
}
