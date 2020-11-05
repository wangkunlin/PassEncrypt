package com.wcg.aab.resguard

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * On 2020-10-22
 */
class ResGuardPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw GradleException("Android Application plugin required")
        }
        project.extensions.create("aabResGuard", AabResGuardExtension)

        project.afterEvaluate {
            AppExtension android = project.android as AppExtension
            android.applicationVariants.each { variant ->
                installResGuardTask(project, variant)
            }
        }
    }

    private static void installResGuardTask(Project project, ApplicationVariant variant) {
        String variantName = variant.name.capitalize()

        String bundleTaskName = "bundle${variantName}"
        Task bundleTask = project.tasks.findByName(bundleTaskName)
        if (bundleTask == null) {
            return
        }
        String aabResGuardTaskName = "aabResguard${variantName}"

        AabResGuardTask aabResGuardTask = project.tasks.create(aabResGuardTaskName, AabResGuardTask)
        aabResGuardTask.variant = variant
        aabResGuardTask.signingConfig = variant.signingConfig
        bundleTask.dependsOn(aabResGuardTask)
        Task packageBundleTask = project.tasks.getByName("package${variantName}Bundle")
        aabResGuardTask.dependsOn(packageBundleTask)

        Task signTask = project.tasks.findByName("sign${variantName}Bundle")
        if (signTask != null) {
            aabResGuardTask.dependsOn(signTask)
        }
    }
}
