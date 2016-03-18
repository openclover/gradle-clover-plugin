package com.atlassian.gradle.plugin.clover.tasks.instrument

import com.atlassian.gradle.plugin.clover.CloverBaseITCase
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class InstrumentTaskITest extends CloverBaseITCase {

    @Test
    public void testInstrumentTaskDisabledWhenCloverDisabled() throws Exception {
        //when
        BuildResult result = runGradle("""
            clover {
                enabled = false
            }
            """, "build")

        //then
        def instrument = result.task(":cloverInstrument").outcome
        def compile = result.task(":compileCloverJava").outcome
        def resources = result.task(":processCloverResources").outcome
        def classes = result.task(":cloverClasses").outcome

        assert instrument == TaskOutcome.SKIPPED
        assert compile == TaskOutcome.SKIPPED
        assert resources == TaskOutcome.SKIPPED
        assert classes == TaskOutcome.SKIPPED
    }

    @Test
    public void testInstrumentCode() throws Exception {
        //having
        testProjectDir.exampleFile()

        //when
        BuildResult result = runGradle("""
            clover {
                instrumentSourceSets = ["other"]
            }
            sourceSets {
                clover {
                    java.srcDirs = ["${testProjectDir.root}${File.separatorChar}clv"]
                }
                other {
                    java.srcDirs = ["${testProjectDir.root}${File.separatorChar}java"]
                }
            }
            """, "cloverInstrument")

        //then

        def instrumentOutcome = result.task(":cloverInstrument").outcome
        def instrumentedFile = new File("${testProjectDir.root}${File.separatorChar}clv${File.separatorChar}SomeTest.java")

        assert instrumentOutcome == TaskOutcome.SUCCESS
        assert instrumentedFile.exists()
        assert instrumentedFile.text.contains("This file has been instrumented by Clover")
    }

    @Test
    public void testSpecifyCloverDatabase() throws Exception {
        //having
        testProjectDir.exampleFile()

        //when
        BuildResult result = runGradle("""
            sourceSets {
                main {
                    java.srcDirs = ["${testProjectDir.root}${File.separatorChar}/java"]
                }
            }

            """, "cloverInstrument")

        //then
        def instrumentOutcome = result.task(":cloverInstrument").outcome
        def cloverDbFile = new File("${testProjectDir.root}/build/clover/clover.db")

        assert instrumentOutcome == TaskOutcome.SUCCESS
        assert cloverDbFile.exists()
        assert cloverDbFile.size() > 0
    }

    @Test
    public void testSkipInstrumentWhenNoSources() throws Exception {
        //having

        //when
        BuildResult result = runGradle("build")

        //then
        def cloverDbFile = new File("${testProjectDir.root}/build/clover/clover.db")

        def instrument = result.task(":cloverInstrument").outcome
        def compile = result.task(":compileCloverJava").outcome
        def resources = result.task(":processCloverResources").outcome
        def classes = result.task(":cloverClasses").outcome

        assert instrument == TaskOutcome.SKIPPED
        assert compile == TaskOutcome.UP_TO_DATE
        assert resources == TaskOutcome.UP_TO_DATE
        assert classes == TaskOutcome.UP_TO_DATE
        assert !cloverDbFile.exists()
    }

}
