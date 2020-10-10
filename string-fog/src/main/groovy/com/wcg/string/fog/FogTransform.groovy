package com.wcg.string.fog

import com.android.build.api.transform.*
import com.android.utils.FileUtils
import com.google.common.collect.ImmutableSet
import com.wcg.string.fog.utils.EncryptString
import com.wcg.string.fog.utils.FogLogger
import com.wcg.string.fog.utils.Utils
import groovy.io.FileType
import org.gradle.api.Project

/**
 * On 2020-09-02
 */
class FogTransform extends Transform {

    private static final String TRANSFORM_NAME = "stringFog"

    private final Project mProject
    private StringFogHandler mFogHandler
    private FogPrinter mFogPrinter
    private boolean mEnabled = false

    FogTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return TRANSFORM_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        def name = QualifiedContent.Scope.PROJECT_LOCAL_DEPS.name()
        def deprecated = QualifiedContent.Scope.PROJECT_LOCAL_DEPS.getClass()
                .getField(name).getAnnotation(Deprecated.class)

        if (deprecated == null) {
            return ImmutableSet.<QualifiedContent.Scope> of(QualifiedContent.Scope.PROJECT
                    , QualifiedContent.Scope.PROJECT_LOCAL_DEPS
                    , QualifiedContent.Scope.EXTERNAL_LIBRARIES
                    , QualifiedContent.Scope.SUB_PROJECTS
                    , QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
        } else {
            return ImmutableSet.<QualifiedContent.Scope> of(QualifiedContent.Scope.PROJECT
                    , QualifiedContent.Scope.EXTERNAL_LIBRARIES
                    , QualifiedContent.Scope.SUB_PROJECTS)
        }
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation invocation) throws TransformException,
            InterruptedException, IOException {
        if (mFogPrinter == null) {
            mFogPrinter = new FogPrinter(new File(mProject.buildDir,
                    "outputs/mapping/${invocation.context.variantName}/stringfog.txt"))
        }

        FogExtension extension = mProject.stringFog as FogExtension
        mEnabled = extension.enabled()
        FogLogger logger = new FogLogger(mProject.logger, extension.log())
        EncryptString.password = extension.password
        mFogHandler = new StringFogHandler(extension, logger, mFogPrinter)

        Set<DirectoryInput> dirInputs = new HashSet<>()
        Set<JarInput> jarInputs = new HashSet<>()

        if (!invocation.incremental) {
            invocation.outputProvider.deleteAll()
        }

        // Collecting inputs.
        invocation.inputs.each { input ->
            input.directoryInputs.each { dirInput ->
                dirInputs.add(dirInput)
            }
            input.jarInputs.each { jarInput ->
                jarInputs.add(jarInput)
            }
        }

        if (!dirInputs.empty) {
            handleDirInput(invocation, dirInputs)
        }

        if (!jarInputs.empty) {
            handleJarInput(invocation, jarInputs)
        }

        mFogPrinter.close()

    }

    private void handleJarInput(TransformInvocation invocation, Set<JarInput> jarInputs) {
        jarInputs.each { jarInput ->
            File jarInputFile = jarInput.file
            File jarOutputFile = invocation.outputProvider.getContentLocation(
                    Utils.uniqueName(jarInputFile), getOutputTypes(), getScopes(), Format.JAR)

            FileUtils.mkdirs(jarOutputFile.parentFile)

            switch (jarInput.status) {
                case Status.NOTCHANGED:
                    if (invocation.incremental) {
                        break
                    }
                case Status.ADDED:
                case Status.CHANGED:
                    if (mEnabled) {
                        mFogHandler.fogJar(jarInputFile, jarOutputFile)
                    } else {
                        FileUtils.copyFile(jarInputFile, jarOutputFile)
                    }
                    break
                case Status.REMOVED:
                    if (jarOutputFile.exists()) {
                        jarOutputFile.delete()
                    }
                    break
            }
        }
    }


    private void handleDirInput(TransformInvocation invocation, Set<DirectoryInput> dirInputs) {
        File dirOutput = invocation.outputProvider.getContentLocation("classes",
                getOutputTypes(), getScopes(), Format.DIRECTORY)
        FileUtils.mkdirs(dirOutput)

        String dirOutputPath = dirOutput.absolutePath

        dirInputs.each { dirInput ->
            String dirInputPath = dirInput.file.absolutePath
            if (invocation.incremental) {
                dirInput.changedFiles.each { entry ->
                    File fileInput = entry.key
                    String path = fileInput.absolutePath
                    String outPath = path.replace(dirInputPath, dirOutputPath)
                    File fileOutput = new File(outPath)
                    FileUtils.mkdirs(fileOutput.parentFile)

                    Status status = entry.value
                    switch (status) {
                        case Status.ADDED:
                        case Status.CHANGED:
                            if (fileInput.file) {
                                if (fileInput.name.endsWith(".class")) {
                                    if (mEnabled) {
                                        mFogHandler.fogClass(fileInput, fileOutput)
                                    } else {
                                        FileUtils.copyFile(fileInput, fileOutput)
                                    }
                                } else {
                                    FileUtils.copyFile(fileInput, fileOutput)
                                }
                            }
                            break
                        case Status.REMOVED:
                            if (fileOutput.exists()) {
                                if (fileOutput.directory) {
                                    fileOutput.deleteDir()
                                } else {
                                    fileOutput.delete()
                                }
                            }
                            break

                    }
                }
            } else {
                dirInput.file.traverse(type: FileType.FILES) { fileInput ->
                    String path = fileInput.absolutePath
                    String outPath = path.replace(dirInputPath, dirOutputPath)
                    File fileOutput = new File(outPath)
                    FileUtils.mkdirs(fileOutput.parentFile)

                    if (fileInput.name.endsWith(".class")) {
                        if (mEnabled) {
                            mFogHandler.fogClass(fileInput, fileOutput)
                        } else {
                            FileUtils.copyFile(fileInput, fileOutput)
                        }
                    } else {
                        FileUtils.copyFile(fileInput, fileOutput)
                    }
                }
            }
        }
    }
}
