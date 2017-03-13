package com.atlassian.gradle.plugin.clover.tasks.merge

import com.atlassian.gradle.plugin.clover.CloverBaseUTCase
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertThat

class CoverageFilesFinderTest extends CloverBaseUTCase {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    def finder

    @Before
    void setUp() {
        super.setUp()
        finder = new CoverageFilesFinder()
    }

    @Test
    void testResolveCoverageFiles() {
        //having
        def expectedFiles = []
        def root = ProjectBuilder.builder()
                .withName("root")
                .withProjectDir(temporaryFolder.root)
                .build()

        for (int i = 0; i < 3; i++) {
            Project project = createSubProject(root, "proj$i")
            expectedFiles += createCloverFiles(project)
        }
        cloverPlugin.apply(root)

        //when
        def result = finder.resolveCloverCoverage(root).collect({ file ->
            file.canonicalPath
        })

        def expected = expectedFiles.collect({ equalTo(it) })
        //then
        assertThat(result, containsInAnyOrder(expected))
    }

    @Test
    void testResolveCoverageFilesWhenCustomizedCloverDbPath() {
        //having
        def expectedFiles = []
        def root = ProjectBuilder.builder()
                .withName("root")
                .withProjectDir(temporaryFolder.root)
                .build()

        Project project = createSubProject(root, "proj1")
        def cloverDir = temporaryFolder.newFolder("otherFolder", "some", "other", "dir")

        def db = new File(cloverDir, "foo.dat")
        db.createNewFile() //create fake database file
        expectedFiles.add(db.canonicalPath)
        project.with {
            clover {
                cloverDatabase = db
            }
        }

        def coverageFile = new File(cloverDir, "foo.datguubn1_ilja440u")
        coverageFile.createNewFile()
        expectedFiles.add(coverageFile.canonicalPath)

        cloverPlugin.apply(root)

        //when
        def result = finder.resolveCloverCoverage(root).collect({ file ->
            file.canonicalPath
        })

        def expected = expectedFiles.collect({ equalTo(it) })
        //then
        assertThat(result, containsInAnyOrder(expected))
    }

    @Test
    void testResolveCloverDatabasesWhenCustomizedCloverDbPath() {
        //having
        def expectedFiles = []
        def root = ProjectBuilder.builder()
                .withName("root")
                .withProjectDir(temporaryFolder.root)
                .build()

        Project project = createSubProject(root, "proj1")
        def cloverDir = temporaryFolder.newFolder("otherFolder", "some", "other", "dir")

        def db = new File(cloverDir, "foo.dat")
        db.createNewFile() //create fake database file
        expectedFiles.add(db.canonicalPath)
        project.with {
            clover {
                cloverDatabase = db
            }
        }

        def coverageFile = new File(cloverDir, "foo.datguubn1_ilja440u")
        coverageFile.createNewFile()

        cloverPlugin.apply(root)

        //when
        def result = finder.resolveCloverDbs(root).collect({ file ->
            file.canonicalPath
        })

        def expected = expectedFiles.collect({ equalTo(it) })
        //then
        assertThat(result, containsInAnyOrder(expected))
    }


    List<String> createCloverFiles(Project project) {
        def files = []

        def cloverDir = new File(project.buildDir, "clover")
        cloverDir.mkdirs()


        def db = new File(cloverDir, "clover.db")
        db.createNewFile()
        files.add(db.canonicalPath)

        def coverageFile = new File(cloverDir, "clover.dbguubn1_ilja440u")
        def globalRecordingFile = new File(cloverDir, "clover.dbguubn1_ilja440u.1")
        def perTestCoverageFile = new File(cloverDir, "clover.dbuubn1_ilja440u_abc.s")

        coverageFile.createNewFile()
        globalRecordingFile.createNewFile()
        perTestCoverageFile.createNewFile()

        files.add(coverageFile.canonicalPath)
        files.add(globalRecordingFile.canonicalPath)
        files.add(perTestCoverageFile.canonicalPath)

        files
    }

    private Project createSubProject(Project root, String projectName) {
        def projectDir = temporaryFolder.newFolder(projectName)
        def project = ProjectBuilder.builder()
                .withName(projectName)
                .withParent(root)
                .withProjectDir(projectDir)
                .build()
        cloverPlugin.apply(project)
        project
    }
}
