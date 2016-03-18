package com.atlassian.gradle.plugin.clover.tasks.instrument

import com.atlassian.gradle.plugin.clover.tasks.CloverLoggableTask
import com.google.common.base.Strings
import com.atlassian.clover.CloverInstr
import com.atlassian.gradle.plugin.clover.tasks.CloverAbstractTask
import com.atlassian.gradle.plugin.clover.util.CloverException
import com.google.common.collect.Iterables
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import static com.atlassian.gradle.plugin.clover.util.CloverUtil.*

/**
 * Task which instruments source code. Takes 2 arguments: <ul>
 * <li> {@link InstrumentTask#outputDir} -> specifies where to put instrumented source code </li>
 * <li> {@link InstrumentTask#instrumentFiles} -> files which will be instrumented by Clover</li>
 * </ul>
 * Will run {@link InstrumentTask#onlyIf} there are any sources to instrument. Require Clover license.
 */
class InstrumentTask extends CloverLoggableTask {

    @OutputDirectory
    File outputDir

    @InputFiles
    FileCollection instrumentFiles

    InstrumentTask() {
        onlyIf({
            def empty = getInstrumentFiles().isEmpty()
            if (empty) {
                logger.info("No sources have been found to instrument")
            }
            !empty
        })
    }

    /**
     * This task action. Perform Clover instrumentation.
     */
    @TaskAction
    void instrument() {
        if (cloverSourceSet(project).allJava.srcDirs.size() > 1) {
            throw new CloverException("Clover sourceSet can't have more than 1 src directory")
        }
        doInstrument()
    }

    private void doInstrument() {
        registerLicenseFile()
        logger.info("Instrumenting sources with Clover")
        CloverInstr.mainImpl(createCliArgs())
    }

    String[] createCliArgs() {
        def cloverExtension = cloverExtension(project)
        final List<String> parameters = []

        parameters.add("-d")
        parameters.add(getOutputDir().absolutePath)

        parameters.add("-i")
        parameters.add(resolveCloverDatabase(project).absolutePath)


        parameters.add("-p")
        parameters.add(cloverExtension.flushPolicy.name())

        if (cloverExtension.flushInterval > 0) {
            parameters.add("-f")
            parameters.add(String.valueOf(cloverExtension.flushInterval))
        }

        if (cloverExtension.debug) {
            parameters.add("-v")
        }

        if (!Strings.isNullOrEmpty(cloverExtension.jdk)) {
            if (cloverExtension.jdk.matches("1\\.[345678]")) {
                parameters.add("--source")
                parameters.add(cloverExtension.jdk)
            } else {
                throw new CloverException("Unsupported java language level version [${cloverExtension.jdk}]. " +
                        "Valid values are [1.3], [1.4], [1.5], [1.6], [1.7] and [1.8]")
            }
        }

        if (!cloverExtension.useFullyQualifiedJavaLang) {
            parameters.add("--dontFullyQualifyJavaLang")
        }

        if (!Strings.isNullOrEmpty(cloverExtension.encoding)) {
            parameters.add("--encoding")
            parameters.add(cloverExtension.encoding)
        }

        parameters.add("--instrlevel")
        parameters.add(cloverExtension.instrumentation.name())

        parameters.add("--instrlambda")
        parameters.add(cloverExtension.instrumentLambda.name())

        getInstrumentFiles().forEach({
            parameters.add(it.absolutePath)
        })

        if (logger.infoEnabled) {
            logger.info("Parameter list being passed to Clover CLI:")
            for (String param : parameters) {
                logger.info("  parameter = [" + param + "]")
            }
        }

        return Iterables.toArray(parameters, String.class)
    }

}
