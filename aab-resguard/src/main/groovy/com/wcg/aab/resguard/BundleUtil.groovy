package com.wcg.aab.resguard

import com.android.build.gradle.internal.tasks.FinalizeBundleTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier

import java.nio.file.Path

/**
 * On 2020-10-22
 */
class BundleUtil {

    static Path getBundleFilePath(Project project, String variantName) {
        String agpVersion = getAgpVersion(project)
        if (agpVersion.startsWith("3.2") || agpVersion.startsWith("3.3")) {
            // AGP3.2.0 - 3.2.1: packageBundle task class is com.android.build.gradle.internal.tasks.BundleTask
            // AGP3.3.0 - 3.3.2: packageBundle task class is com.android.build.gradle.internal.tasks.PackageBundleTask
            return getBundleFor32To33(project, variantName)
        } else if (agpVersion.startsWith("3.4") || agpVersion.startsWith("3.5")) {
            // AGP3.4.0+: use FinalizeBundleTask sign bundle file
            // packageBundle task bundleLocation is intermediates dir
            // The finalize bundle file path: FinalizeBundleTask.finalBundleLocation
            return getBundleFor34To35(project, variantName)
        } else {
            // AGP3.6+: removed finalBundleLocation field, and finalBundleFile is public field
            return getBundleFileForAGP36After(project, variantName)
        }
    }

    static Path getBundleFileForAGP36After(Project project, String variantName) {
        // use FinalizeBundleTask to sign bundle file
        FinalizeBundleTask finalizeBundleTask = project.tasks.getByName("sign${variantName}Bundle")
        // FinalizeBundleTask.finalBundleFile is the final bundle path
        File file = finalizeBundleTask.finalBundleFile.get().getAsFile()
        return file.toPath()
    }

    static Path getBundleFor34To35(Project project, String variantName) {
        // use FinalizeBundleTask to sign bundle file
        Task finalizeBundleTask = project.tasks.getByName("sign${variantName}Bundle")
        // FinalizeBundleTask.finalBundleFile is the final bundle path
        File location = finalizeBundleTask.finalBundleLocation as File
        File file = new File(location, finalizeBundleTask.finalBundleFileName as String)
        return file.toPath()
    }

    static Path getBundleFor32To33(Project project, String variantName) {
        String bundleTaskName = "package${variantName}Bundle"
        Task bundleTask = project.tasks.getByName(bundleTaskName)
        File file = new File(bundleTask.bundleLocation as File, bundleTask.fileName as String)
        return file.toPath()
    }

    static String getAgpVersion(Project project) {
        String agpVersion = null
        def configuration = project.rootProject.buildscript.configurations.getByName(ScriptHandler.CLASSPATH_CONFIGURATION)
        def artifacts = configuration.resolvedConfiguration.resolvedArtifacts
        artifacts.each { artifact ->
            def identifier = artifact.id.componentIdentifier
            if (identifier instanceof DefaultModuleComponentIdentifier) {
                if (identifier.group == "com.android.tools.build" || identifier.group.hashCode() == 432891823) {
                    if (identifier.module == "gradle") {
                        agpVersion = identifier.version
                    }
                }
            }
        }
        if (agpVersion == null) {
            throw GradleException("get AGP version failed")
        }
        return agpVersion
    }
}
