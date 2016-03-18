package com.atlassian.gradle.plugin.clover.util

import com.atlassian.gradle.plugin.clover.CloverConstants
import com.atlassian.gradle.plugin.clover.config.CloverPluginExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.SourceSet

class CloverUtil {

    static SourceSet mainSourceSet(Project project) {
        sourceSet(project, SourceSet.MAIN_SOURCE_SET_NAME)
    }
    
    static SourceSet testSourceSet(Project project) {
        sourceSet(project, SourceSet.TEST_SOURCE_SET_NAME)
    }

    static SourceSet cloverSourceSet(Project project) {
        sourceSet(project, CloverConstants.CLOVER_SOURCE_SETS)
    }

    static SourceSet cloverTestSourceSet(Project project) {
        sourceSet(project, CloverConstants.CLOVER_TEST_SOURCE_SETS)
    }

    static SourceSet sourceSet(Project project, String sourceSet) {
        javaConvention(project).sourceSets.getByName(sourceSet)
    }

    static JavaPluginConvention javaConvention(Project project) {
        project.convention.getPlugin(JavaPluginConvention.class)
    }

    static CloverPluginExtension cloverExtension(Project project) {
        project.extensions.getByName(CloverConstants.CLOVER_EXTENSION) as CloverPluginExtension
    }

    static ReportingExtension reportingExtension(Project project) {
        project.extensions.getByName(ReportingExtension.NAME) as ReportingExtension
    }

    static File resolveCloverDatabase(Project project) {
        cloverExtension(project).cloverDatabase ?: new File(project.buildDir, "clover${File.separatorChar}clover.db")
    }

    static File resolveCloverDatabaseDir(Project project) {
        cloverExtension(project).cloverDatabase ?
                cloverExtension(project).cloverDatabase.parentFile : new File(project.buildDir, "clover")
    }

}
