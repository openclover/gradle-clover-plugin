package com.atlassian.gradle.plugin.clover.tasks.test

import com.atlassian.clover.CloverNames
import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import com.atlassian.gradle.plugin.clover.util.SourcesTestRule
import org.junit.Rule
import org.junit.Test

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.cloverSourceSet
import static com.atlassian.gradle.plugin.clover.util.CloverUtil.mainSourceSet

class TestWithCloverTaskUTest extends CloverBaseUTCase {

    @Rule
    public SourcesTestRule sourcesTestRule = new SourcesTestRule();

    @Test
    public void testCloverEnabledTestClassPath() throws Exception {
        def project = javaProject()
        cloverPlugin.apply(project)
        sourcesTestRule.exampleFile()

        cloverSourceSet(project).java.srcDir(sourcesTestRule.root)

        //when
        def task = project.tasks.findByName("testWithClover") as TestWithCloverTask
        task.execute()

        //then
        def test = project.tasks.findByName("test") as org.gradle.api.tasks.testing.Test

        def mainClasses = new File(project.buildDir, "classes${File.separatorChar}main")
        def cloverClasses = new File(project.buildDir, "classes${File.separatorChar}clover")

        assert !test.classpath.contains(mainClasses)
        assert test.classpath.contains(cloverClasses)
    }

    @Test
    public void testCloverDisabledTestClassPath() throws Exception {
        //having
        def project = javaProject()
        cloverPlugin.apply(project)
        project.with {
            clover {
                enabled = false
            }
        }

        //when
        def task = project.tasks.findByName("testWithClover") as TestWithCloverTask
        task.execute()

        //then
        def test = project.tasks.findByName("test") as org.gradle.api.tasks.testing.Test

        def mainClasses = new File(project.buildDir, "classes${File.separatorChar}main")
        def cloverClasses = new File(project.buildDir, "classes${File.separatorChar}clover")

        assert test.classpath.contains(mainClasses)
        assert !test.classpath.contains(cloverClasses)
    }

    @Test
    public void testCloverDebugLoggingLevelDebug() throws Exception {
        //having
        def project = javaProject()
        cloverPlugin.apply(project)
        project.with {
            clover {
                debug = true
            }
        }

        //when
        def task = project.tasks.findByName("testWithClover") as TestWithCloverTask
        task.execute()

        //then
        def test = project.tasks.findByName("test") as org.gradle.api.tasks.testing.Test
        def result = test.jvmArgs

        result.contains("-D${CloverNames.PROP_LOGGING_LEVEL}=debug")
    }

}
