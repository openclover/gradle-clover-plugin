package com.atlassian.gradle.plugin.clover

import com.atlassian.gradle.plugin.clover.actions.CloverConfigurationsAction
import com.atlassian.gradle.plugin.clover.actions.CloverDefaultConfigurationProjectAction
import com.atlassian.gradle.plugin.clover.actions.CloverDependencyProjectAction
import com.atlassian.gradle.plugin.clover.actions.CloverTasksAction
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.ReportingBasePlugin

class CloverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyRequiredPlugins(project)

        new CloverConfigurationsAction().configure(project)
        new CloverTasksAction().configure(project)

        addEvaluationCallbacks(project)
    }

    void addEvaluationCallbacks(Project project) {
        project.afterEvaluate(new CloverDependencyProjectAction())
        project.afterEvaluate(new CloverDefaultConfigurationProjectAction())
    }

    void applyRequiredPlugins(Project project) {
        project.pluginManager.apply(JavaPlugin.class)
        project.pluginManager.apply(ReportingBasePlugin.class)
    }

}
