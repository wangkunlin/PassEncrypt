package com.wcg.apk.reinforce

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class ReinforcePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }

        ReinforceExtension extension = project.extensions.create("reinforce", ReinforceExtension)
        project.extensions.add("resguard", new ResguardExtension("resguard"))
        project.afterEvaluate {
            if (!extension.disable) {
                if (extension.sid == null || extension.sid.isEmpty()) {
                    throw new IllegalStateException("sid is null.")
                }
                if (extension.skey == null || extension.skey.isEmpty()) {
                    throw new IllegalStateException("skey is null.")
                }
            }
            def variants = project.android.applicationVariants
            variants.all { variant ->
//                boolean debuggable = variant.buildType.debuggable
//                if (!debuggable) {
                installReinforce(project, variant)
//                }
            }
        }
    }

    private static void installReinforce(Project project, def variant) {

        String reinforceTaskName = "reinforce${variant.name.capitalize()}"

        ReinforceTask reinforceTask = project.tasks.create(reinforceTaskName, ReinforceTask) {
            group = "reinforce"
            description = "reinforce apk task"
        }

        def scope = variant.variantData.scope
        File apkDir = scope.apkLocation
        String apkName = variant.variantData.outputScope.mainSplit.outputFileName
        File apkFile = new File(apkDir, apkName)
        File reinforceDir = new File(apkDir, "reinforce")

        reinforceTask.reinforceDir = reinforceDir
        reinforceTask.apkFile = apkFile
        reinforceTask.setVariant(variant)

        String assembleTaskName = "assemble${variant.name.capitalize()}"
        Task assembleTask = project.tasks.getByName(assembleTaskName)

        reinforceTask.dependsOn(assembleTask)

    }
}
