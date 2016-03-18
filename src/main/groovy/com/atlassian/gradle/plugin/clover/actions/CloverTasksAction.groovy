package com.atlassian.gradle.plugin.clover.actions

import com.atlassian.gradle.plugin.clover.tasks.instrument.InstrumentTask
import com.atlassian.gradle.plugin.clover.tasks.merge.CoverageFilesFinder
import com.atlassian.gradle.plugin.clover.tasks.merge.MergeTask
import com.atlassian.gradle.plugin.clover.tasks.report.ReportTask
import com.atlassian.gradle.plugin.clover.tasks.test.TestWithCloverTask
import org.gradle.api.Project

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.*

class CloverTasksAction {

    void configure(Project project) {
        createInstrumentMainSourcesTask(project)
        createInstrumentTestSourcesTask(project)
        setupTestsWithClover(project)
        createMergeTask(project)
        createReportTask(project)

        disableCloverTasksIfNotNeeded(project)
        setupLicenseMappings(project)
    }

    private void createInstrumentMainSourcesTask(Project project) {
        def cloverPluginExtension = cloverExtension(project)

        def cloverInstrument = project.tasks.create("cloverInstrument", InstrumentTask.class)
        cloverInstrument.onlyIf({
            cloverExtension(project).automaticIntegration
        })

        project.tasks.getByName("cloverClasses").dependsOn cloverInstrument
        project.tasks.getByName("build").dependsOn "cloverClasses"

        cloverInstrument.conventionMapping.map("instrumentFiles") {
            project.files(cloverPluginExtension.instrumentSourceSets.collect {
                sourceSet(project, it).allJava
            })
        }

        cloverInstrument.conventionMapping.map("outputDir") {
            cloverSourceSet(project).java.srcDirs.find()
        }
    }

    private void createInstrumentTestSourcesTask(Project project) {
        def cloverPluginExtension = cloverExtension(project)

        def cloverTestInstrument = project.tasks.create("cloverTestInstrument", InstrumentTask.class)
        cloverTestInstrument.onlyIf({
            cloverPluginExtension.automaticIntegration && cloverPluginExtension.includesTestSourceRoots
        })

        project.tasks.getByName("cloverTestClasses").dependsOn cloverTestInstrument
        project.tasks.getByName("test").dependsOn "cloverTestClasses"

        cloverTestInstrument.conventionMapping.map("instrumentFiles") {
            testSourceSet(project).allJava
        }

        cloverTestInstrument.conventionMapping.map("outputDir") {
            cloverTestSourceSet(project).java.srcDirs.find()
        }
    }

    private setupLicenseMappings(Project project) {
        def cloverPluginExtension = cloverExtension(project)


        def licenseConventionMapping = { task ->
            task.conventionMapping.map("licenseLocation") {
                cloverPluginExtension.licenseLocation
            }

            task.conventionMapping.map("licenseCert") {
                cloverPluginExtension.license
            }
        }
        project.tasks.withType(InstrumentTask.class).all(licenseConventionMapping)
        project.tasks.withType(ReportTask.class).all(licenseConventionMapping)
    }

    def createReportTask(Project project) {
        def cloverPluginExtension = cloverExtension(project)

        project.tasks.create("cloverReport", ReportTask.class)

        project.tasks.withType(ReportTask.class).all({ task ->

            if (!project.subprojects.isEmpty()) {
                task.dependsOn 'cloverMerge'
            }
            task.conventionMapping.map("cloverDb") {
                if (!project.subprojects.isEmpty()) {
                    resolveDbForMultiProjectReport(project)
                } else {
                    resolveCloverDatabase(project)
                }
            }

            task.conventionMapping.map("reportDirectory") {
                cloverPluginExtension.report.outputDir ?:
                        new File(reportingExtension(project).baseDir, "clover")
            }
        })
    }

    private def resolveDbForMultiProjectReport(Project project) {
        if (cloverExtension(project).cloverDatabase != null) {
            project.logger.info("For root project {} resolved database {} explicitly setup in configuration closure",
                    project.name, cloverExtension(project).cloverDatabase.absolutePath)
            return cloverExtension(project).cloverDatabase
        }

        def mergeDb = new File(resolveCloverMergeDb(project))
        if (mergeDb.exists()) {
            project.logger.info("For root project {} found merged database {}. Using it for generating report",
                    project.name, mergeDb.absolutePath)
            return mergeDb
        }
        return resolveCloverDatabase(project)
    }

    private void setupTestsWithClover(Project project) {
        project.tasks.create("testWithClover", TestWithCloverTask.class) {
            project.tasks.findByName("test").dependsOn it
        }
    }

    private void disableCloverTasksIfNotNeeded(Project project) {
        def cloverPluginExtension = cloverExtension(project)

        project.tasks.findByName("compileCloverJava").onlyIf { cloverPluginExtension.enabled }
        project.tasks.findByName("processCloverResources").onlyIf { cloverPluginExtension.enabled }
        project.tasks.findByName("cloverClasses").onlyIf { cloverPluginExtension.enabled }
    }

    private createMergeTask(Project project) {
        if (!project.subprojects.isEmpty()) {
            project.tasks.create("cloverMerge", MergeTask.class)
            project.tasks.withType(MergeTask.class).all({ task ->
                task.conventionMapping.map("cloverMergeDb") {
                    def cloverMergeDb = resolveCloverMergeDb(project)
                    project.logger.info("Resolved merged database for project {} to file {} ", project.name, cloverMergeDb)
                    project.file(cloverMergeDb)
                }
                task.conventionMapping.map("coverageFiles") {
                    def list = CoverageFilesFinder.resolveCloverCoverage(project).asList()
                    project.logger.info("Resolved coverage files for merging databases {}", list)
                    list
                }
                task.conventionMapping.map("cloverDatabases") {
                    def list = CoverageFilesFinder.resolveCloverDbs(project).asList()
                    project.logger.info("Resolved coverage databases for merging databases {}", list)
                    list
                }
            })
        }
    }

    private def resolveCloverMergeDb(Project project) {
        "${resolveCloverDatabaseDir(project).absolutePath}${File.separatorChar}cloverMerge.db"
    }

}
