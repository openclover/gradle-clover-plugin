package com.atlassian.gradle.plugin.clover.actions

import com.atlassian.gradle.plugin.clover.CloverConstants
import com.atlassian.gradle.plugin.clover.config.CloverPluginExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.*

class CloverConfigurationsAction {

    void configure(Project project) {
        project.extensions.create(CloverConstants.CLOVER_EXTENSION, CloverPluginExtension.class);

        JavaPluginConvention javaConvention = javaConvention(project);

        javaConvention.sourceSets.create(CloverConstants.CLOVER_SOURCE_SETS) {
            it.java.srcDirs = [("${project.buildDir}${File.separatorChar}$CloverConstants.DEFAULT_INSTR_DIR")]
            it.resources.srcDirs = mainSourceSet(project).resources.srcDirs
        }

        javaConvention.sourceSets.create(CloverConstants.CLOVER_TEST_SOURCE_SETS) {
            it.java.srcDirs = [("${project.buildDir}${File.separatorChar}$CloverConstants.DEFAULT_TEST_INSTR_DIR")]
            it.resources.srcDirs = testSourceSet(project).resources.srcDirs
            it.compileClasspath = project.files(cloverSourceSet(project).output,
                    project.getConfigurations().getByName(CloverConstants.CLOVER_TEST_COMPILE_CONFIGURATION_NAME))
            it.setRuntimeClasspath(project.files(cloverTestSourceSet(project).output,
                    mainSourceSet(project).output,
                    project.getConfigurations().getByName(CloverConstants.CLOVER_TEST_RUNTIME_CONFIGURATION_NAME)));
        }

        project.configurations.getByName(CloverConstants.CLOVER_COMPILE_CONFIGURATION_NAME) {
            description CloverConstants.CLOVER_COMPILE_CFG_DESC
            extendsFrom project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
        }
        project.configurations.getByName(CloverConstants.CLOVER_TEST_COMPILE_CONFIGURATION_NAME) {
            extendsFrom project.configurations.getByName(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME)
        }

        project.configurations.getByName(CloverConstants.CLOVER_RUNTIME_CONFIGURATION_NAME) {
            extendsFrom project.configurations.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME)
        }

        project.configurations.getByName(CloverConstants.CLOVER_TEST_RUNTIME_CONFIGURATION_NAME) {
            extendsFrom project.configurations.getByName(JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME)
        }

        project.configurations.create(CloverConstants.CLOVER_CONFIGURATION_NAME) {
            description CloverConstants.CLOVER_CFG_DESC
            extendsFrom project.configurations.getByName(CloverConstants.CLOVER_COMPILE_CONFIGURATION_NAME)
        }

        project.dependencies.add(CloverConstants.CLOVER_CONFIGURATION_NAME, cloverSourceSet(project).output)
    }

}
