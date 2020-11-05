package com.wcg.only.debug

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.*

/**
 * On 2020-10-22
 */
class CheckDebugPlugin implements Plugin<Project> {

    private static final String PUBLISHING = "publishing"

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw GradleException("Android Application plugin required")
        }
        AppExtension android = project.android as AppExtension

        // disable add new build type
        android.buildTypes.whenObjectAdded {
            throw new GradleException("not support add new build type")
        }

        // disable add new flavor
        android.productFlavors.whenObjectAdded {
            throw new GradleException("not support add new flavor")
        }

        project.gradle.taskGraph.beforeTask(new Action<Task>() {
            @Override
            void execute(Task task) {
                def startTaskNames = project.gradle.startParameter.taskNames
                boolean publishingStart = false
                startTaskNames.forEach { startTaskName ->
                    if (startTaskName.startsWith(PUBLISHING)) {
                        publishingStart = true
                    }
                }
                if (publishingStart) {
                    return
                }
                String taskName = task.name.toLowerCase()
                if (taskName.contains("release")) {
                    throw new GradleException("Can not build project with variant name: release")
                }
            }
        })

        project.afterEvaluate {

            def taskNames = project.gradle.startParameter.taskNames
            android.applicationVariants.each { variant ->
                checkBuildAndInstallTask(project, variant, taskNames)
            }
        }
    }

    private static void checkBuildAndInstallTask(Project project, ApplicationVariant variant, List<String> taskNames) {

        String variantLower = variant.name.toLowerCase()
        String variantName = variant.name.capitalize()

        boolean debuggable = variant.buildType.debuggable
        if (debuggable) {
            if (variantLower == "release") {
                throw new GradleException("release build type debuggable must to false")
            }
            return
        }

        if (variantLower == "debug") {
            throw new GradleException("debug build type debuggable must to true")
        }

        installTask(project, "assemble", variantName)
        installTask(project, "resguard", variantName)
        installTask(project, "reinforce", variantName)
        installTask(project, "bundle", variantName)

        if (taskNames == null || taskNames.empty) {
            return
        }
        taskNames.forEach { taskName ->
            String lowerTaskName = taskName.toLowerCase()
            if (lowerTaskName.contains(variantLower)) {
                if (!lowerTaskName.contains(PUBLISHING)) {
                    throw new GradleException("Can not build project with variant name: ${variant.name}")
                }
            }
        }
    }

    private static void installTask(Project project, String preffix, String variantName) {
        String taskName = "${preffix}${variantName}"
        def task = project.tasks.findByName(taskName)
        if (task == null) {
            return
        }
        Task publishTask = project.tasks.create("${PUBLISHING}${taskName.capitalize()}", DefaultTask)
        publishTask.group = PUBLISHING
        publishTask.outputs.upToDateWhen { false }
        publishTask.dependsOn(task)
    }
}
