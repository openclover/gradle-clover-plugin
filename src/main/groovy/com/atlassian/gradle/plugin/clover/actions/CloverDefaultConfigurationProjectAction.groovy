package com.atlassian.gradle.plugin.clover.actions

import com.atlassian.gradle.plugin.clover.CloverConstants
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.cloverExtension

/**
 * Action which configures {@link Dependency#DEFAULT_CONFIGURATION} configuration to let it extends from Clover one.
 * This behavior is required in multi-project environment to enable cross-project coverage recording.
 */
class CloverDefaultConfigurationProjectAction implements Action<Project> {

    @Override
    void execute(Project project) {
        if (shouldRun(project)) {
            project.configurations.getByName(Dependency.DEFAULT_CONFIGURATION)
                    .extendsFrom(project.configurations.getByName(CloverConstants.CLOVER_CONFIGURATION_NAME))
        }
    }

    private boolean shouldRun(Project project) {
        def cloverExt = cloverExtension(project)
        cloverExt.enabled && cloverExt.automaticIntegration
    }
}
