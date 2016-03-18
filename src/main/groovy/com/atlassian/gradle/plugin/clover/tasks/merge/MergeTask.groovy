package com.atlassian.gradle.plugin.clover.tasks.merge

import com.atlassian.clover.CloverMerge
import com.atlassian.gradle.plugin.clover.tasks.CloverLoggableTask
import com.google.common.collect.Iterables
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.cloverExtension

/**
 * Merge multiple clover databases into one.
 */
class MergeTask extends CloverLoggableTask {

    @OutputFile
    File cloverMergeDb

    @InputFiles
    List<File> cloverDatabases
    @InputFiles
    List<File> coverageFiles

    MergeTask() {
        description = "Merges multiple Clover databases and coverage files into one aggregated Clover database"
    }

    @TaskAction
    void merge() {
        def files = getCloverDatabases()
        if (files.size() > 0) {
            def cloverExt = cloverExtension(project)

            def parameters = [] as List;
            if (cloverExt.debug) {
                parameters.add("--debug")
            }
            parameters.add("-i");
            parameters.add(getCloverMergeDb().absolutePath);

            parameters.addAll(files.collect({ it.absolutePath }))

            logger.info("Calling CloverMerge with params: {}", parameters)
            if (CloverMerge.mainImpl(Iterables.toArray(parameters, String.class)) != 0) {
                throw new GradleException("Merging Clover databases failed")
            }
        }
    }
}
