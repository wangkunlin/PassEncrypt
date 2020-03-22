package com.spoon.string.encrypt

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.tasks.GenerateBuildConfig
import com.android.build.gradle.tasks.GenerateResValues
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.jetbrains.annotations.NotNull
/**
 * Created by wangkunlin
 * On 2020-03-21
 */
class StringEncryptPlugin implements Plugin<Project> {

    @Override
    void apply(@NotNull Project project) {

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin
        def hasLibraryPlugin = project.plugins.hasPlugin LibraryPlugin

        // Ensure the Android plugin has been added in app or library form, but not both.
        if (!hasAppPlugin && !hasLibraryPlugin) {
            throw new IllegalStateException("The 'android' or 'android-library' plugin is required.")
        } else if (hasAppPlugin && hasLibraryPlugin) {
            throw new IllegalStateException("Having both 'android' and 'android-library' plugin is not supported.")
        }

        StringEncryptExtension extension = project.extensions.create "stringEncrypt", StringEncryptExtension

        project.afterEvaluate {
            def variants = hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
            variants.all { variant ->
                String name = variant.name
                name = name.substring(0, 1).toUpperCase() + name.substring(1)
                String moduleName = project.name

                String resId = moduleName.toLowerCase() + "_spoon_psw"

                // generateDebugBuildConfig
                GenerateBuildConfig buildConfigTask = project.tasks.getByName("generate" + name + "BuildConfig")
                String packageName = buildConfigTask.buildConfigPackageName
                File sourceDir = buildConfigTask.sourceOutputDir
                buildConfigTask.doLast(new Action<Task>() {
                    @Override
                    void execute(Task task) {
                        StringDecryptGenerator generator = new StringDecryptGenerator(sourceDir,
                                packageName, moduleName, extension, resId)
                        generator.generate()
                    }
                })

                // 找到 形如 generateReleaseResValues 的 task
                GenerateResValues resValuesTask = project.tasks.getByName("generate" + name + "ResValues")
                File curDir = project.buildscript.sourceFile.parentFile
                File resDir = resValuesTask.resOutputDir
                resValuesTask.doLast(new Action<Task>() {
                    @Override
                    void execute(Task task) {
                        StringValueGenerator generator = new StringValueGenerator(curDir, resDir, resId, extension)
                        generator.generate()
                    }
                })
            }
        }
    }
}
