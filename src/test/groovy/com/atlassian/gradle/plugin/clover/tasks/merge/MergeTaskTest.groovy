package com.atlassian.gradle.plugin.clover.tasks.merge

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class MergeTaskTest extends CloverBaseUTCase {

    @Test
    public void testCloverMergeNotAddedWhenSimpleProject() throws Exception {
        //having
        def project = javaProject()
        cloverPlugin.apply(project)

        //when
        def cloverMerge = project.tasks.findByName('cloverMerge')

        //then
        assert cloverMerge == null
    }

    @Test
    public void testCloverMergeNotUpToDateWhenCloverDbsSpecified() throws Exception {
        //having
        def aggregatedDbPath = new File(getClass().classLoader.getResource("databases/aggregated.db").file)
        def aggregated2DbPath = new File(getClass().classLoader.getResource("databases/aggregated2.db").file)

        def project = javaProject()
        ProjectBuilder.builder()
                .withParent(project)
                .build()

        project.with {
            allprojects {
                cloverPlugin.apply(it)
            }
        }
        def cloverMerge = project.tasks.findByName('cloverMerge')

        cloverMerge.cloverDatabases = [aggregatedDbPath, aggregated2DbPath]

        cloverMerge.execute()

        def db = cloverMerge.cloverMergeDb
        assert db.exists()
    }

}