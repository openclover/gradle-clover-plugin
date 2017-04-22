package com.atlassian.gradle.plugin.clover.tasks.test

import com.atlassian.gradle.plugin.clover.CloverBaseITCase
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class TestWithCloverTaskITest extends CloverBaseITCase {

    @Test
    public void testTestWithCloverCoverageDataRecorded() throws Exception {
        //having
        testProjectDir.exampleFile()
        testProjectDir.exampleTestFile()
        //when
        def result = runGradle("""
            sourceSets {
                main {
                    java {
                        srcDirs = ["${testProjectDir.root}${File.separatorChar}java"]
                    }
                }
                test {
                    java {
                        srcDirs = ["${testProjectDir.root}${File.separatorChar}test"]
                     }
                }
            }
            test {
                jvmArgs "-Dclover.logging.level=debug"
            }
            dependencies {
                testCompile group: 'junit', name: 'junit', version: '4.+'
                cloverCompile group: 'com.atlassian.clover', name: 'clover', version: '${cloverJarVersion}'
            }
            """, "test")

        //then
        assert result.task(":cloverInstrument").outcome == TaskOutcome.SUCCESS
        assert result.task(":cloverClasses").outcome == TaskOutcome.SUCCESS
        assert result.task(":test").outcome == TaskOutcome.SUCCESS

        def cloverDir = new File(testProjectDir.root, "build${File.separatorChar}clover")
        def coverageFile = cloverDir.listFiles().find({ it.name =~ "clover\\.db.+\$" })
        assert coverageFile != null
    }

    @Test
    public void testTestWithCloverSkippedWhenNoSources() throws Exception {
        //having

        //when
        def result = runGradle("test")

        //then
        assert result.task(":testWithClover").outcome == TaskOutcome.SKIPPED
        assert result.task(":test").outcome == TaskOutcome.NO_SOURCE
    }

}
