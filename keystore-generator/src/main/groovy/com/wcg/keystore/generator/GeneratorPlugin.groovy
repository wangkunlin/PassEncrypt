package com.wcg.keystore.generator

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * On 2020-07-25
 */
class GeneratorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }

        project.afterEvaluate {
            def variants = project.android.applicationVariants
            variants.all { variant ->
                String name = variant.name.capitalize()

                String taskName = "minify${name}WithProguard"
                def task = project.tasks.findByName(taskName)
                installGeneratorTask(project, variant, task)

                taskName = "minify${name}WithR8"
                task = project.tasks.findByName(taskName)
                installGeneratorTask(project, variant, task)
            }
        }

    }

    private static void installGeneratorTask(Project project, ApplicationVariant variant, Task task) {
        if (task == null) {
            return
        }
        if (!variant.signingReady || variant.signingConfig == null) {
            return
        }
        String generatorTaskName = "generat${variant.name.capitalize()}Keystore"

        GeneratorTask generatorTask = project.tasks.create(generatorTaskName, GeneratorTask)
        generatorTask.keystoreFile = variant.signingConfig.storeFile
        generatorTask.variant = variant

        task.dependsOn(generatorTask)
    }
}
