package com.atlassian.gradle.plugin.clover.tasks

import com.atlassian.clover.Logger
import com.atlassian.gradle.plugin.clover.util.GradleCloverLogger

abstract class CloverLoggableTask extends CloverAbstractTask {

    def final cloverLogger

    CloverLoggableTask() {
        cloverLogger = new GradleCloverLogger(project.logger)
        Logger.factory = { cloverLogger } as Logger.Factory
    }
}
