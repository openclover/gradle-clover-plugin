package com.atlassian.gradle.plugin.clover.tasks.test

import com.atlassian.clover.CloverNames
import com.atlassian.gradle.plugin.clover.CloverConstants
import com.atlassian.gradle.plugin.clover.tasks.CloverAbstractTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.*

class TestWithCloverTask extends CloverAbstractTask {

    TestWithCloverTask() {
        inputs.files cloverSourceSet(project).output
        onlyIf {
            !cloverSourceSet(project).allSource.isEmpty()
        }
    }

    @TaskAction
    def modifyTestClasspath() {
        def cloverCfg = cloverExtension(project)
        def testTask = project.tasks.findByName("test") as Test
        if (cloverCfg.includesTestSourceRoots) {
            testTask.classpath =
                    cloverTestSourceSet(project).output +
                            project.configurations.getByName(CloverConstants.CLOVER_TEST_RUNTIME_CONFIGURATION_NAME) +
                            project.configurations.getByName(CloverConstants.CLOVER_CONFIGURATION_NAME)
        } else {
            testTask.classpath =
                    sourceSet(project, "test").output +
                            project.configurations.getByName("testRuntime") +
                            project.configurations.getByName(CloverConstants.CLOVER_CONFIGURATION_NAME)
        }
        logger.debug("Test with Clover for project {} is enabled, changing test task classpath to cloverized one: {}",
                project.name, testTask.classpath.files)

        if (cloverCfg.debug) {
            testTask.jvmArgs("-D${CloverNames.PROP_LOGGING_LEVEL}=debug")
        }
    }
}
