package com.atlassian.gradle.plugin.clover.config

import static com.atlassian.gradle.plugin.clover.config.ReportType.HTML
import static com.atlassian.gradle.plugin.clover.config.ReportType.XML

class CloverReportExtension {

    Set<ReportType> reportTypes = [HTML, XML] as Set

    File outputDir

    def reportType(ReportType reportType) {
        reportTypes.add(reportType)
    }
}
