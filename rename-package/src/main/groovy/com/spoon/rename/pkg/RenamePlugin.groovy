package com.spoon.rename.pkg

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RenamePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }

        RenameExtension extension = project.extensions.create("renamePackage", RenameExtension)

        project.afterEvaluate {
            if (extension.applicationId != null && !extension.applicationId.empty) {
                AppExtension android = project.android
                def variants = android.applicationVariants
                variants.all { variant ->
                    boolean debuggable = variant.buildType.debuggable
                    if (!debuggable) {
                        if (variant.variantData.metaClass.respondsTo(variant, "variantConfiguration") ||
                                variant.variantData.metaClass.hasProperty(variant, "variantConfiguration")) {
                            variant.variantData.variantConfiguration.defaultConfig.applicationId = extension.applicationId
                            variant.variantData.variantConfiguration.mergedFlavor.applicationId = extension.applicationId
                        } else if (variant.variantData.metaClass.respondsTo(variant, "variantDslInfo") ||
                                variant.variantData.metaClass.hasProperty(variant, "variantDslInfo")) {
                            variant.variantData.variantDslInfo.defaultConfig.applicationId = extension.applicationId
                            variant.variantData.variantDslInfo.mergedFlavor.applicationId = extension.applicationId
                        }
                    }
                }
            }
        }
    }
}