package com.wcg.manifest.editor.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.tasks.ManifestProcessorTask
import com.wcg.manifest.editor.Utils
import com.wcg.manifest.editor.extension.ManifestExtension
import com.wcg.manifest.editor.run.EditAction
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * On 2020-06-12
 */
class ManifestPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }

        project.extensions.create("editManifest", ManifestExtension)

        project.afterEvaluate {
            def variants = project.android.applicationVariants
            variants.all { variant ->

                ManifestExtension extension = project.extensions.findByName("editManifest")

                String name = variant.name
                name = name.capitalize()

                ManifestProcessorTask task = project.tasks.findByName("process${name}Manifest")
                task.inputs.property("editManifestExtension", Utils.getMD5(extension.toString()))
                task.doLast(new EditAction(extension))
            }
        }

    }
}
