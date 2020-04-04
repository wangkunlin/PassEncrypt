package com.spoon.rename.pkg

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
            def variants = project.android.applicationVariants
            variants.all { variant ->
                boolean debuggable = variant.buildType.debuggable
                if (!debuggable) {
//                    project.android.defaultConfig.applicationId = extension.applicationId
                    variant.variantData.variantConfiguration.defaultConfig.applicationId = extension.applicationId
                    variant.variantData.variantConfiguration.mergedFlavor.applicationId = extension.applicationId
                }
            }
        }
    }
}