package com.atlassian.gradle.plugin.clover.plugin

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import com.atlassian.gradle.plugin.clover.CloverConstants
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class CloverExtensionTest extends CloverBaseUTCase {

    @Test
    public void testCloverPluginAddsCloverExtension() throws Exception {
        //having
        def project = ProjectBuilder.builder().build()

        //when
        cloverPlugin.apply(project)

        //then
        def cloverConvention = project.extensions.getByName(CloverConstants.CLOVER_EXTENSION)
        assert cloverConvention != null
    }

    @Test
    public void testCanModifyCloverExtensionLicenseLocation() throws Exception {
        //having
        def project = ProjectBuilder.builder().build()
        def license = "/tmp/some/location/clover.license"

        //when
        cloverPlugin.apply(project)

        project.with {
            clover {
                licenseLocation = license
            }
        }

        //then
        def cloverConvention = project.extensions.getByName(CloverConstants.CLOVER_EXTENSION)
        assert cloverConvention.licenseLocation == license
    }

}
