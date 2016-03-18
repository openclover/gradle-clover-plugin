package com.atlassian.gradle.plugin.clover.actions

import com.atlassian.gradle.plugin.clover.CloverBaseITCase
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class CloverDependencyProjectActionITest extends CloverBaseITCase {

    @Test
    public void testCloverSourcesCompilesWhereNoCloverJarProvided() throws Exception {
        //having
        testProjectDir.exampleFile()

        //when
        BuildResult result = runGradle("""
            sourceSets {
                main {
                    java.srcDirs = ["${testProjectDir.root}${File.separatorChar}java"]
                }
            }
            """, "cloverClasses")

        //then
        def instrumentOutcome = result.task(":cloverInstrument").outcome
        def cloverClassesOutcome = result.task(":cloverClasses").outcome

        assert instrumentOutcome == TaskOutcome.SUCCESS
        assert cloverClassesOutcome == TaskOutcome.SUCCESS
    }
}
