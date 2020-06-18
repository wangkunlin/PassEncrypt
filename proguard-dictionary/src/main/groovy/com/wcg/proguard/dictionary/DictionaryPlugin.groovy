package com.wcg.proguard.dictionary

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.tasks.ProguardConfigurableTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * On 2020-06-13
 */
class DictionaryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // LinkApplicationAndroidResourcesTask
        // LinkAndroidResForBundleTask
        // MergeAaptProguardFilesCreationAction
        // ProguardConfigurableTask
        // ExportConsumerProguardFilesTask
        // MergeGeneratedProguardFilesCreationAction

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }

        project.extensions.create("dictionary", DictionaryExtension)

        project.afterEvaluate {
            AppExtension android = project.android

            def variants = android.applicationVariants
            variants.all { variant ->
                String name = variant.name
                name = name.capitalize()
                String taskName = "minify${name}WithProguard"
                def task = project.tasks.findByName(taskName)
                installTask(project, variant, task)

                taskName = "minify${name}WithR8"
                task = project.tasks.findByName(taskName)
                installTask(project, variant, task)
            }
        }

    }

    private static void installTask(Project project, def variant, ProguardConfigurableTask task) {
        if (task == null) {
            return
        }
        File proguardFile = new File(project.buildDir, "intermediates/proguard-dictionary/${variant.dirName}/proguard.txt")
        File dictionaryFile = new File(project.buildDir, "intermediates/proguard-dictionary/${variant.dirName}/dictionary.txt")
        def files = project.files(proguardFile)
        task.configurationFiles.from(files)

        GenerateProguardDictionaryTask dicTask = project.tasks.create("generate${variant.name.capitalize()}ProguardDictionary",
                GenerateProguardDictionaryTask)
        dicTask.proguardFile = proguardFile
        dicTask.dictionaryFile = dictionaryFile
        dicTask.proguardTask = task

        task.dependsOn(dicTask)
    }
}
