package com.atlassian.gradle.plugin.clover

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before

class CloverBaseUTCase {


    protected CloverPlugin cloverPlugin

    @Before
    public void setUp() throws Exception {
        cloverPlugin = new CloverPlugin()
    }

    protected Project javaProject() {
        def project = ProjectBuilder.builder().build()
        project.pluginManager.apply "java"
        project
    }
}
