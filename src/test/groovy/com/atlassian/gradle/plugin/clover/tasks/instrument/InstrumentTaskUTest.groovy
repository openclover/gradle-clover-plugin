package com.atlassian.gradle.plugin.clover.tasks.instrument

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import com.atlassian.gradle.plugin.clover.CloverConstants
import com.atlassian.gradle.plugin.clover.config.FlushPolicy
import com.atlassian.gradle.plugin.clover.config.InstrumentLambdaLevel
import com.atlassian.gradle.plugin.clover.config.InstrumentationLevel
import com.atlassian.gradle.plugin.clover.util.CloverException
import com.atlassian.gradle.plugin.clover.util.CloverUtil
import org.junit.Test

class InstrumentTaskUTest extends CloverBaseUTCase {

    @Test
    public void testInstrumentTaskOutputMatchCloverSourceSetDir() throws Exception {
        //having
        def project = javaProject()

        //when
        cloverPlugin.apply(project)

        //then
        def instrumentTask = project.tasks.find { it.name == "cloverInstrument" }
        def result = instrumentTask.outputs.getFiles().singleFile
        def expectedResult = project.file("${project.buildDir}${File.separatorChar}${CloverConstants.DEFAULT_INSTR_DIR}")

        assert result == expectedResult
    }


    @Test(expected = CloverException.class)
    public void testInstrumentTaskFailsWhenMultipleCloverSourceSets() throws Exception {
        //having
        def project = javaProject()

        //when
        cloverPlugin.apply(project)

        project.with {
            sourceSets {
                clover {
                    java.srcDir "src${File.separatorChar}other-gen"
                }
            }
        }

        //then
        def instrumentTask = project.tasks.find { it.name == "cloverInstrument" }
        instrumentTask.instrument() //expect failure
    }

    @Test
    public void testInstrumentCreateCliArgs() throws Exception {
        //having
        def project = javaProject()
        cloverPlugin.apply(project)
        def instrumentTask = project.tasks.find { it.name == "cloverInstrument" }
        def cloverExtension = CloverUtil.cloverExtension(project)

        cloverExtension.with {
            cloverDatabase = new File("/tmp/dbClover.db")
            debug = true
            encoding = "ISO-2222"
            flushInterval = 1111
            flushPolicy = FlushPolicy.INTERVAL
            instrumentation = InstrumentationLevel.STATEMENT
            instrumentLambda = InstrumentLambdaLevel.ALL
            jdk = "1.8"
            useFullyQualifiedJavaLang = false
        }

        //when
        def result = instrumentTask.createCliArgs()

        //then
        assert result.contains("/tmp/dbClover.db")
        assert result.contains("-v")
        assert result.contains("ISO-2222")
        assert result.contains("1111")
        assert result.contains("INTERVAL")
        assert result.contains("STATEMENT")
        assert result.contains("ALL")
        assert result.contains("1.8")
        assert result.contains("--dontFullyQualifyJavaLang")
    }
}