package com.wcg.aab.resguard

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.scope.VariantScope
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
                VariantScope scope = variant.variantData.scope
                installResGuardTask(project, variant, scope)
            }
        }
    }

    private static void installResGuardTask(Project project, ApplicationVariant variant, VariantScope scope) {
        String variantName = variant.name.capitalize()
        variant.outputs.each {

        }
        String bundleTaskName = "bundle${variantName}"
        Task bundleTask = project.tasks.findByName(bundleTaskName)
        if (bundleTask == null) {
            return
        }
        String aabResGuardTaskName = "aabresguard${variantName}"

        AabResGuardTask aabResGuardTask = project.tasks.create(aabResGuardTaskName, AabResGuardTask)
        aabResGuardTask.variant = variant
        aabResGuardTask.variantScope = scope
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
