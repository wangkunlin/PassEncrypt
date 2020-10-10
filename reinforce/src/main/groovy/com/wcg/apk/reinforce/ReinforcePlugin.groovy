package com.wcg.apk.reinforce

import com.android.build.VariantOutput
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.scope.ApkData
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
            if (extension.enabled()) {
                if (extension.sid == null || extension.sid.isEmpty()) {
                    throw new IllegalStateException("sid is null.")
                }
                if (extension.skey == null || extension.skey.isEmpty()) {
                    throw new IllegalStateException("skey is null.")
                }
            }
            AppExtension android = project.android
            def variants = android.applicationVariants
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
        String apkName

        if (variant.variantData.metaClass.respondsTo(variant, "outputScope") ||
                variant.variantData.metaClass.hasProperty(variant, "outputScope")) {
            apkName = variant.variantData.outputScope.mainSplit.outputFileName
        } else if (variant.variantData.metaClass.respondsTo(variant, "outputFactory") ||
                variant.variantData.metaClass.hasProperty(variant, "outputFactory")) {
            def apkList = variant.variantData.outputFactory.apkDataList
            int n = apkList.size()
            for (int i = 0; i < n; i++) {
                ApkData apkData = apkList.get(i)
                if (apkData.type == VariantOutput.OutputType.MAIN) {
                    apkName = apkData.outputFileName
                }
            }
        }
        if (apkName == null) {
            throw new IllegalStateException("not found apk name")
        }
//        project.logger.lifecycle("get apk name ${apkName}")
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
