package com.wcg.string.fog

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * On 2020-09-02
 */
class StringFogPlugin implements Plugin<Project> {

    private static final String XXTEA = "org.xxtea:xxtea-java:1.0.5"

    @Override
    void apply(Project project) {

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }

        project.dependencies.add("implementation", XXTEA)
        project.extensions.create("stringFog", FogExtension)

        def android = project.android as AppExtension
        android.registerTransform(new FogTransform(project))

    }

}
