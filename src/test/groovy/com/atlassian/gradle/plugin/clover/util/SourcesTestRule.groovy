package com.atlassian.gradle.plugin.clover.util

import com.google.common.base.Joiner
import org.junit.rules.TemporaryFolder

class SourcesTestRule extends TemporaryFolder {
    public static final String PROJECT_1 = "project1"
    public static final String PROJECT_2 = "project2"

    public static final String TEST_DIR = "test"
    public static final String JAVA_DIR = "java"

    void exampleFile() {
        File folder = newFolder(JAVA_DIR)
        sourceFile(folder, "SomeTest.java", """public class SomeTest {
                                public void helloWorld() {
                                    System.out.println("HelloWorld");
                                }
                            }""")
    }

    void exampleFile(File projectRoot) {
        File folder = subProjectJavaRoot(projectRoot)
        sourceFile(folder, "SomeTest.java", """public class SomeTest {
                                public void helloWorld() {
                                    System.out.println("HelloWorld");
                                }
                            }""")
    }

    void exampleTestFile() {
        File folder = newFolder(TEST_DIR)
        sourceFile(folder, "SomeTestTest.java", """public class SomeTestTest {
                                @org.junit.Test
                                public void helloWorld() {
                                    new SomeTest().helloWorld();
                                }
                            }""")
    }

    void exampleTestFile(File projectRoot) {
        File folder = subProjectTestRoot(projectRoot)
        sourceFile(folder, "SomeTestTest.java", """public class SomeTestTest {
                                @org.junit.Test
                                public void helloWorld() {
                                    new SomeTest().helloWorld();
                                }
                            }""")
    }

    Map<String, File> exampleMultiProjectProject(List<String> projects, Map<String, List<String>> projectDependencies) {
        Map<String, File> projectsRoots = projects.collectEntries { project ->
            [(project): exampleSubProject(root, project)]
        }

        projectDependencies.forEach { project, dependencies ->
            def depenedenciesString = Joiner.on('\n').join(dependencies.collect {
                "compile project(':$it')"
            })

            sourceFile(projectsRoots[project],
                    "build.gradle",
                    //PluginDependencySpec doesn't work currently in allproject closure
                    "plugins { id 'clover'} \r\n dependencies {$depenedenciesString}")
        }

        def includes = Joiner.on('\n').join(projects.collect {
            "include ':${it}'"
        })

        sourceFile(root, "settings.gradle", includes)

        return projectsRoots
    }


    File exampleSubProject(File root, String subProjectName) {
        def subProjectRoot = new File(root, subProjectName)
        def javaRoot = subProjectJavaRoot(subProjectRoot)
        def testRoot = subProjectTestRoot(subProjectRoot)
        javaRoot.mkdirs()
        testRoot.mkdirs()
        return subProjectRoot
    }

    private File subProjectJavaRoot(File subProjectRoot) {
        new File(subProjectRoot, "src${File.separatorChar}main${File.separatorChar}java")
    }

    private File subProjectTestRoot(File subProjectRoot) {
        new File(subProjectRoot, "src${File.separatorChar}test${File.separatorChar}java")
    }

    void sourceFile(File folder, String sourceFileName, String fileContent) {
        File someTest = new File(folder, sourceFileName)
        someTest.text = fileContent
    }

    void sourceFile(String sourceFileName, String fileContent) {
        File someTest = newFile(sourceFileName)
        someTest.text = fileContent
    }

}
