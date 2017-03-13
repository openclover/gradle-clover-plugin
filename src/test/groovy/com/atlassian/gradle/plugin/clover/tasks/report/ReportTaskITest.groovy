package com.atlassian.gradle.plugin.clover.tasks.report

import com.atlassian.gradle.plugin.clover.CloverBaseITCase
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class ReportTaskITest extends CloverBaseITCase {

    def aggregatedDbPath = getClass().classLoader.getResource("databases/aggregated.db").file

    @Test
    public void testCloverAllTypesCurrentReport() throws Exception {
        //when
        BuildResult result = runGradle("""
            cloverReport.cloverJarPath = \"${cloverJarClassPath()}\"
            import com.atlassian.gradle.plugin.clover.config.ReportType
            clover {
                cloverDatabase = file("${getClass().classLoader.getResource("databases/aggregated.db").file}")
                report {
                    reportType ReportType.XML
                    reportType ReportType.PDF
                    reportType ReportType.JSON
                }
            }
            """, "cloverReport")

        //then
        def report = result.task(":cloverReport").outcome

        def html = new File("${testProjectDir.root}/build/reports/clover/index.html")
        def xml = new File("${testProjectDir.root}/build/reports/clover/clover.xml")
        def pdf = new File("${testProjectDir.root}/build/reports/clover/clover.pdf")
        def json = new File("${testProjectDir.root}/build/reports/clover/clover.json")

        assert report == TaskOutcome.SUCCESS

        assert html.exists() && html.size() > 0
        assert xml.exists() && xml.size() > 0
        assert pdf.exists() && pdf.size() > 0
        assert json.exists() && json.size() > 0
    }

    @Test
    public void testCloverReportDoesntRunWhenCloverDisabled() throws Exception {
        //when
        BuildResult result = runGradle("""
            cloverReport.cloverJarPath = \"${cloverJarClassPath()}\"
            clover {
                enabled = false
                cloverDatabase = file("${getClass().classLoader.getResource("databases/aggregated.db").file}")
            }

            build.finalizedBy "cloverReport"
            """, "build")

        //then
        def report = result.task(":cloverReport").outcome

        def xml = new File("${testProjectDir.root}/build/reports/clover/clover.xml")

        assert report == TaskOutcome.SKIPPED
        assert !xml.exists()
    }

    @Test
    public void testCloverReportRunsWhenCloverDisabledAndInvokedDirectly() throws Exception {
        //when
        BuildResult result = runGradle("""
            cloverReport.cloverJarPath = \"${cloverJarClassPath()}\"
            clover {
                enabled = false
                cloverDatabase = file("${getClass().classLoader.getResource("databases/aggregated.db").file}")
            }

            """, "cloverReport")

        //then
        def report = result.task(":cloverReport").outcome

        def xml = new File("${testProjectDir.root}/build/reports/clover/clover.xml")

        assert report == TaskOutcome.SUCCESS
        assert xml.exists()
    }

    @Test
    public void testCloverReportRunsWhenCloverDisabledAndInvokedDirectlyWithMultipleTasks() throws Exception {
        //when
        BuildResult result = runGradle("""
            cloverReport.cloverJarPath = \"${cloverJarClassPath()}\"
            clover {
                enabled = false
                cloverDatabase = file("${aggregatedDbPath}")
            }

            """, ["build", "cloverReport"])

        //then
        def report = result.task(":cloverReport").outcome

        def xml = new File("${testProjectDir.root}/build/reports/clover/clover.xml")

        assert report == TaskOutcome.SUCCESS
        assert xml.exists()
    }

    @Test
    public void testCloverReportRunsWhenCloverEnabledAndInvokedDirectlyWithMultipleTasks() throws Exception {
        //when
        BuildResult result = runGradle("""
            cloverReport.cloverJarPath = \"${cloverJarClassPath()}\"
            clover {
                cloverDatabase = file("$aggregatedDbPath")
            }

            """, ["build", "cloverReport"])

        //then
        def report = result.task(":cloverReport").outcome

        def xml = new File("${testProjectDir.root}/build/reports/clover/clover.xml")

        assert report == TaskOutcome.SUCCESS
        assert xml.exists()
    }

    @Test
    public void testUpdateCloverDbForReportTaskOnly() throws Exception {
        //when
        BuildResult result = runGradle("""
            cloverReport.cloverJarPath = \"${cloverJarClassPath()}\"
            cloverReport.cloverDb = file("$aggregatedDbPath")
            """, ["build", "cloverReport"])

        //then
        def report = result.task(":cloverReport").outcome

        def xml = new File("${testProjectDir.root}/build/reports/clover/clover.xml")

        assert report == TaskOutcome.SUCCESS
        assert xml.exists()
    }


}
