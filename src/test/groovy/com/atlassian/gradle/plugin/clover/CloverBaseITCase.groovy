package com.atlassian.gradle.plugin.clover

import com.atlassian.gradle.plugin.clover.actions.CloverDependencyProjectAction
import com.atlassian.gradle.plugin.clover.util.SourcesTestRule
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule

import static clover.com.google.common.base.Strings.isNullOrEmpty

class CloverBaseITCase {

    @Rule
    public final SourcesTestRule testProjectDir = new SourcesTestRule();

    protected CloverPlugin cloverPlugin
    protected static
    final String cloverJarVersion = System.getProperty("cloverJarVersion") ?: CloverDependencyProjectAction.CLOVER_JAR_VERSION

    @Before
    public void setUp() throws Exception {
        cloverPlugin = new CloverPlugin()
    }

    protected BuildResult runGradle(String argument) {
        runGradle("", [argument])
    }

    protected BuildResult runGradle(String script, String argument) {
        runGradle(script, [argument])
    }

    protected BuildResult runGradle(String script, List<String> arguments) {
        List<File> pluginClasspath = pluginClasspath()

        def text = """
                      plugins {
                            id 'java'
                            id 'clover'
                      }
                      allprojects {
                        repositories { 
                            mavenLocal()
                            mavenCentral() 
                        }} \r\n""" + script
        testProjectDir.sourceFile("build.gradle", text)

        def debugRunner = Boolean.parseBoolean(System.getProperty("debug.gradle.runner", "false"))
        println "Effective debug gradle runner value is: $debugRunner"
        if (isNullOrEmpty(System.getProperty("GRADLE_USER_HOME"))) {
            println """WARN: When GRADLE_USER_HOME property not set to your local .gradle directory.
                    Tests may fail with dependency resolution or lack of repositories errors."""
        }
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath(pluginClasspath)
                .withDebug(debugRunner)
                .withArguments(arguments + ["-Dgradle.user.home=${System.getProperty("GRADLE_USER_HOME")}"] as String[])
                .forwardOutput()
                .build()
    }


    protected List<File> pluginClasspath() {
        URL pluginClasspathResource = pluginClassPathResource()
        def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }
        pluginClasspath
    }

    protected URL pluginClassPathResource() {
        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Could not find plugin-classpath.txt resource, run `createClassPathManifest` build task from Gradle.")
        }
        pluginClasspathResource
    }

    protected String cloverJarClassPath() {
        pluginClassPathResource().readLines().find({ it.matches(".*clover.*jar") })
    }
}
