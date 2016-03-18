package com.atlassian.gradle.plugin.clover.util

import org.junit.rules.TemporaryFolder

class SourcesTestRule extends TemporaryFolder {
    public static final String TEST_DIR = "test"
    public static final String JAVA_DIR = "java"

    void exampleFile() {
        def folder = newFolder(JAVA_DIR)
        sourceFile(folder, "SomeTest.java", """public class SomeTest {
                                public void helloWorld() {
                                    System.out.println("HelloWorld");
                                }
                            }""")
    }

    void exampleTestFile() {
        def folder = newFolder(TEST_DIR)
        sourceFile(folder, "SomeTestTest.java", """public class SomeTestTest {
                                @org.junit.Test
                                public void helloWorld() {
                                    new SomeTest().helloWorld();
                                }
                            }""")
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
