package com.atlassian.gradle.plugin.clover.plugin

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import com.atlassian.gradle.plugin.clover.CloverConstants
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.SelfResolvingDependency
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.cloverSourceSet
import static com.atlassian.gradle.plugin.clover.util.CloverUtil.javaConvention
import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertThat

class CloverPluginTest extends CloverBaseUTCase {

    @Test
    void testCloverPluginAddProperTasks() {
        def project = ProjectBuilder.builder().build()
        cloverPlugin.apply(project)

        assert project.tasks.findByName("cloverInstrument") != null
    }

    @Test
    void testCloverPluginAppliesCloverCompileConfiguration() {
        //having
        def junitDependency = "org.junit:junit:4.0.0"

        def project = javaProject()
        project.dependencies {
            compile junitDependency
        }

        //when
        cloverPlugin.apply(project)

        //then
        def cloverCompileConfig = project.configurations.find({
            it.name == CloverConstants.CLOVER_COMPILE_CONFIGURATION_NAME
        })

        assert cloverCompileConfig != null

        assert cloverCompileConfig.description == CloverConstants.CLOVER_COMPILE_CFG_DESC

        def dependencyStringsCloverCompile = cloverCompileConfig.allDependencies.collect {
            "$it.group:$it.name:$it.version".toString()
        }

        assert dependencyStringsCloverCompile.contains(junitDependency)
    }

    @Test
    void testCloverPluginAppliesCloverConfiguration() {
        //having
        def junitDependency = "org.junit:junit:4.0.0"

        def project = javaProject()
        project.dependencies {
            compile junitDependency
        }

        //when
        cloverPlugin.apply(project)

        //then
        def cloverConfig = project.configurations.find({ it.name == CloverConstants.CLOVER_CONFIGURATION_NAME })

        assert cloverConfig != null

        assert cloverConfig.description == CloverConstants.CLOVER_CFG_DESC


        def externalDependenciesClover = cloverConfig.allDependencies.findAll { dependency ->
            dependency instanceof ExternalModuleDependency
        }
        .collect { dependency ->
            "$dependency.group:$dependency.name:$dependency.version".toString()
        }

        def filesDependenciesClover = cloverConfig.dependencies.findAll { dependency ->
            dependency instanceof SelfResolvingDependency
        }.collect { dependency ->
            dependency.resolve()
        }.flatten()

        def cloverOutputFiles = cloverSourceSet(project).output.files.collect({
            equalTo(it)
        })

        assert externalDependenciesClover.contains(junitDependency)
        assertThat(filesDependenciesClover, containsInAnyOrder(cloverOutputFiles))
    }

    @Test
    void testCloverPluginCreatesSourceSets() {
        //having
        def project = javaProject()
        def javaConvention = javaConvention(project);

        //when
        cloverPlugin.apply(project)

        javaConvention.sourceSets {
            main.java.srcDir "src/gen-src/"
        }

        //then
        def cloverSourceSets = cloverSourceSet(project)

        assert cloverSourceSets != null
        assert cloverSourceSets.compileConfigurationName == CloverConstants.CLOVER_COMPILE_CONFIGURATION_NAME
    }

}
