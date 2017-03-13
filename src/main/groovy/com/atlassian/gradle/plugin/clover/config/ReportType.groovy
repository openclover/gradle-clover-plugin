package com.atlassian.gradle.plugin.clover.config

enum ReportType {
    HTML(false),
    XML(true),
    PDF(true),
    JSON(true)

    ReportType(boolean summaryReport) {
        this.summaryReport = summaryReport
    }

    public final boolean summaryReport
}
