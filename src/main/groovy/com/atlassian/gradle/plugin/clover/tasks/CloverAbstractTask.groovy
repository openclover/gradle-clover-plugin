package com.atlassian.gradle.plugin.clover.tasks

import com.atlassian.clover.CloverNames
import com.google.common.base.Strings
import org.gradle.api.DefaultTask

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.cloverExtension

class CloverAbstractTask extends DefaultTask {

    String licenseLocation;
    String licenseCert

    CloverAbstractTask() {
        onlyIf {
            cloverExtension(project).enabled
        }
    }

    protected void registerLicenseFile() {
        if (!Strings.isNullOrEmpty(getLicenseCert())) {
            System.setProperty(CloverNames.PROP_LICENSE_CERT, getLicenseCert())
            return
        }
        if (!Strings.isNullOrEmpty(getLicenseLocation())) {
            System.setProperty(CloverNames.PROP_LICENSE_PATH, getLicenseLocation())
            return
        }
    }
}
