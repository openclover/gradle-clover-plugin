package com.atlassian.gradle.plugin.clover.actions

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import org.junit.Before
import org.junit.Test

class CloverDependencyProjectActionUTest extends CloverBaseUTCase {

    def dependencyAction

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dependencyAction = new CloverDependencyProjectAction()
    }

    @Test
    public void testAddCloverDependency() throws Exception {
        //having
        def project = javaProject()
        project.with {
            repositories {
                mavenLocal()
            }
        }
        cloverPlugin.apply(project)

        //when
        dependencyAction.execute(project)

        //then
        def cloverDep = project.configurations.findByName("cloverCompile").allDependencies.find {
            it.group == "com.atlassian.clover" && it.name == "clover"
        }

        assert cloverDep != null
        assert cloverDep.version == CloverDependencyProjectAction.CLOVER_JAR_VERSION
    }

    @Test
    public void testDontAddDependencyWhenAddedToCompileConfig() throws Exception {
        //having
        def project = javaProject()
        cloverPlugin.apply(project)

        project.with {
            repositories {
                mavenLocal()
            }
            dependencies {
                compile "com.atlassian.clover:clover:4.0.6"
            }
        }

        //when
        dependencyAction.execute(project)

        //then
        def cloverDep = project.configurations.findByName("cloverCompile").allDependencies.find {
            it.group == "com.atlassian.clover" && it.name == "clover"
        }

        assert cloverDep != null
        assert cloverDep.version == "4.0.6"
    }

    @Test
    public void testDontAddDependencyWhenAddedToCloverCompileConfig() throws Exception {
        //having
        def project = javaProject()
        cloverPlugin.apply(project)

        project.with {
            repositories {
                mavenLocal()
            }
            dependencies {
                cloverCompile "com.atlassian.clover:clover:4.0.6"
            }
        }

        //when
        dependencyAction.execute(project)

        //then
        def cloverDep = project.configurations.findByName("cloverCompile").allDependencies.find {
            it.group == "com.atlassian.clover" && it.name == "clover"
        }

        assert cloverDep != null
        assert cloverDep.version == "4.0.6"
    }

    @Test
    public void testDontAddDependencyWhenNoRepositories() throws Exception {
        //having
        def project = javaProject()
        cloverPlugin.apply(project)

        //when
        dependencyAction.execute(project)

        //then
        def cloverDep = project.configurations.findByName("cloverCompile").allDependencies.find {
            it.group == "com.atlassian.clover" && it.name == "clover"
        }

        assert cloverDep == null
    }
}
