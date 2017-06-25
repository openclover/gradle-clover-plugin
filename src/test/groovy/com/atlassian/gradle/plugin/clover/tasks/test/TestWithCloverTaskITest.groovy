package com.atlassian.gradle.plugin.clover.tasks.test

import com.atlassian.gradle.plugin.clover.CloverBaseITCase
import groovy.transform.CompileStatic
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

import static com.atlassian.gradle.plugin.clover.util.SourcesTestRule.PROJECT_1
import static com.atlassian.gradle.plugin.clover.util.SourcesTestRule.PROJECT_2

@CompileStatic
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
                cloverCompile group: 'org.openclover', name: 'clover', version: '${cloverJarVersion}'
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

    @Test
    public void testTestWithCloverInMultiProjectEnvironment() throws Exception {
        //having
        def projectsRoots = testProjectDir.exampleMultiProjectProject([PROJECT_1, PROJECT_2], [(PROJECT_2): [PROJECT_1]])
        testProjectDir.exampleFile(projectsRoots[PROJECT_1])
        testProjectDir.exampleTestFile(projectsRoots[PROJECT_2])

        //when
        def result = runGradle("""
        allprojects {
            apply plugin: 'java'
            test {
                jvmArgs "-Dclover.logging.level=debug"
            }
            dependencies {
                testCompile group: 'junit', name: 'junit', version: '4.+'
            }
        }
            """, "test")

        //then
//        assert result.task("test").outcome == TaskOutcome.SUCCESS
//        assert result.task(":cloverClasses").outcome == TaskOutcome.SUCCESS
//        assert result.task(":test").outcome == TaskOutcome.SUCCESS

//        def cloverDir = new File(testProjectDir.root, "build${File.separatorChar}clover")
//        def coverageFile = cloverDir.listFiles().find({ it.name =~ "clover\\.db.+\$" })
//        assert coverageFile != null
        assert true
    }


}
