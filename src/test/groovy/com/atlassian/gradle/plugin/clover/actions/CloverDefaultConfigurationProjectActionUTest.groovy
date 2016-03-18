package com.atlassian.gradle.plugin.clover.actions

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import com.atlassian.gradle.plugin.clover.CloverConstants
import org.gradle.api.artifacts.Dependency
import org.junit.Before
import org.junit.Test

class CloverDefaultConfigurationProjectActionUTest extends CloverBaseUTCase {

    def action

    @Override
    @Before
    void setUp() throws Exception {
        super.setUp();
        action = new CloverDefaultConfigurationProjectAction()
    }

    @Test
    void testWhenAutomaticIntegrationDisabledDefaultConfigNotChanged() {
        //when
        def project = javaProject()
        cloverPlugin.apply(project)

        project.with {
            clover.enabled = true
            clover.automaticIntegration = false
        }

        //then
        action.execute(project)

        def configs = project.configurations
                .getByName(Dependency.DEFAULT_CONFIGURATION)
                .getExtendsFrom()
                .collect({ config ->
            config.name
        })

        assert !configs.contains(CloverConstants.CLOVER_CONFIGURATION_NAME)
    }

    @Test
    void testWhenCloverDisabledDefaultConfigNotChanged() {
        //when
        def project = javaProject()
        cloverPlugin.apply(project)

        project.with {
            clover.enabled = false
            clover.automaticIntegration = true
        }

        //then
        action.execute(project)

        def configs = project.configurations
                .getByName(Dependency.DEFAULT_CONFIGURATION)
                .getExtendsFrom()
                .collect({ config ->
            config.name
        })

        assert !configs.contains(CloverConstants.CLOVER_CONFIGURATION_NAME)
    }


    @Test
    void testWhenAutomaticIntegrationEnabledDefaultConfigExtendsFromClover() {
        //when
        def project = javaProject()
        cloverPlugin.apply(project)

        project.with {
            clover.enabled = true
            clover.automaticIntegration = true
        }

        //then
        action.execute(project)

        def configs = project.configurations
                .getByName(Dependency.DEFAULT_CONFIGURATION)
                .getExtendsFrom()
                .collect({ config ->
            config.name
        })

        assert configs.contains(CloverConstants.CLOVER_CONFIGURATION_NAME)
    }

}
