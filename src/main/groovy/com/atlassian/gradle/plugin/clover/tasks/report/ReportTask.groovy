package com.atlassian.gradle.plugin.clover.tasks.report

import com.atlassian.gradle.plugin.clover.config.ReportType
import com.atlassian.gradle.plugin.clover.tasks.CloverAbstractTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import static com.atlassian.gradle.plugin.clover.config.ReportType.*
import static com.atlassian.gradle.plugin.clover.util.CloverUtil.cloverExtension
import static com.google.common.base.Strings.isNullOrEmpty

/**
 * Generates Clover reports. Uses clover database and Clover coverage recordings files.
 * Can be configured by {@link com.atlassian.gradle.plugin.clover.config.CloverReportExtension}
 */
class ReportTask extends CloverAbstractTask {
    protected def cloverJarPath

    @InputFile
    File cloverDb

    @OutputDirectory
    File reportDirectory;

    ReportTask() {
        onlyIf = {
            def shouldRun = cloverExtension(project).enabled
            try {
                shouldRun |= reportTaskCliCalled(project)
            } catch (Exception e) {
                logger.info("Could not read current task requests", e)
            }
            if (!shouldRun) {
                logger.info("Skipping current task because Clover is disabled and this task wasn't called directly")
            }
            shouldRun
        } as Spec<Task>
    }

    boolean reportTaskCliCalled(Project project) {
        project.gradle.startParameter.getTaskRequests().collect { it.args }.flatten().contains("cloverReport")
    }

    private void initAntTasks() {
        if (isNullOrEmpty(cloverJarPath)) {
            cloverJarPath = project.getConfigurations().getByName("cloverCompile").asPath
        }
        ant.taskdef(resource: 'cloverlib.xml', classpath: cloverJarPath)
    }

    @TaskAction
    def generateReport() {
        initAntTasks()
        registerLicenseFile()

        def reportExt = cloverExtension(project).report

        reportExt.reportTypes.each { reportType ->
            createReport(outputForReportType(reportType), reportType)
        }
    }

    File outputForReportType(ReportType reportType) {
        switch (reportType) {
            case HTML:
                return getReportDirectory()
            case XML:
                return new File(getReportDirectory(), "clover.xml")
            case PDF:
                return new File(getReportDirectory(), "clover.pdf")
            case JSON:
                return new File(getReportDirectory(), "clover.json")
        }
    }

    void createReport(File outfile, ReportType reportType) {
        ant."clover-report"(initString: getCloverDb().absolutePath) {
            current(outfile: outfile, summary: reportType.summaryReport) {
                format(type: reportType.name())
            }
        }
    }
}
