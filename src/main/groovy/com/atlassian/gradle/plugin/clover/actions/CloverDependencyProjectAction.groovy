package com.atlassian.gradle.plugin.clover.actions

import com.atlassian.gradle.plugin.clover.CloverConstants
import com.atlassian.gradle.plugin.clover.util.CloverUtil
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPlugin

class CloverDependencyProjectAction implements Action<Project> {

    static final String CLOVER_JAR_VERSION = "4.1.2"
    private static final Logger log = Logging.getLogger(CloverDependencyProjectAction.class)

    @Override
    void execute(Project project) {
        if (shouldExecute(project)) {
            def compileDepend = dependsOnCloverJar(project, JavaPlugin.COMPILE_CONFIGURATION_NAME)
            def cloverDepend = dependsOnCloverJar(project, CloverConstants.CLOVER_COMPILE_CONFIGURATION_NAME)
            if (!(compileDepend || cloverDepend)) {
                log.info("Adding dependency on Clover to ${CloverConstants.CLOVER_COMPILE_CONFIGURATION_NAME} configuration")
                project.dependencies.add(CloverConstants.CLOVER_COMPILE_CONFIGURATION_NAME, "com.atlassian.clover:clover:${CLOVER_JAR_VERSION}")
            }
        }
    }

    private boolean shouldExecute(Project project) {
        CloverUtil.cloverExtension(project).enabled && CloverUtil.cloverExtension(project).automaticIntegration && declaresAnyRepos(project)
    }

    boolean declaresAnyRepos(Project project) {
        project.repositories.size() > 0
    }

    private boolean dependsOnCloverJar(Project project, String configName) {
        !project.configurations.getByName(configName).allDependencies.matching(isCloverDependency()).isEmpty()
    }

    private Closure<Boolean> isCloverDependency() {
        { dependency ->
            dependency.group == "com.atlassian.clover" && dependency.name == "clover"
        }
    }
}
