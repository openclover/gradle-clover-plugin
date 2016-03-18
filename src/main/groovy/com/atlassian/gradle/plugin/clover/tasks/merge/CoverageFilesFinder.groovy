package com.atlassian.gradle.plugin.clover.tasks.merge

import com.atlassian.gradle.plugin.clover.util.CloverUtil
import org.gradle.api.Project

class CoverageFilesFinder {

    public static List<File> resolveCloverCoverage(Project project) {
        project.logger.info("Resolving coverage files for project {}", project.name)
        def files = []

        project.subprojects.each { p ->
            def subProjectCoverage = project.fileTree(dir: CloverUtil.resolveCloverDatabaseDir(p).absolutePath)
            project.logger.info("Found coverage files {} for sub-project {}", subProjectCoverage.files, p.name)
            files.addAll(subProjectCoverage.files)
        }
        files
    }

    public static List<File> resolveCloverDbs(Project project) {
        def files = []
        project.subprojects.each { p ->
            def file = CloverUtil.resolveCloverDatabase(p)
            if (file.exists()) {
                files.add(file)
            }
        }
        files
    }

}
