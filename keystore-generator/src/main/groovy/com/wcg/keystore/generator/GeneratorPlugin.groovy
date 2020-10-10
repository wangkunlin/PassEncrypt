package com.wcg.keystore.generator

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.SigningConfig
import com.android.builder.core.BuilderConstants
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * On 2020-07-25
 */
class GeneratorPlugin implements Plugin<Project> {

    static String SIGNING_CONFIG_NAME = "keystore-generator-signing-config"
    static String KEYSTORE_NAME = "keystore.jks"

    @Override
    void apply(Project project) {

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }

        def android = project.android as AppExtension

        // android add debug and release build types: ApplicationVariantFactory.createDefaultComponents()
        android.buildTypes.all { buildType ->
            if (buildType.name != BuilderConstants.DEBUG) {
                buildType.signingConfig = genSigningConfigForBuildType(project, buildType)
            }
        }

        // build.gradle add others build types
        android.buildTypes.whenObjectAdded(new Action<BuildType>() {
            @Override
            void execute(BuildType buildType) {
                buildType.signingConfig = genSigningConfigForBuildType(project, buildType)
            }
        })

        project.afterEvaluate {
            def variants = android.applicationVariants
            variants.each { variant ->

                String name = variant.name.capitalize()

                String taskName = "validateSigning${name}"
                def task = project.tasks.findByName(taskName)
                installGeneratorTask(project, variant, task)
            }
        }

    }

    private static SigningConfig genSigningConfigForBuildType(Project project, BuildType buildType) {
        String name = buildType.name
        def file = project.file("${name}-${KEYSTORE_NAME}.psw")

        String keyPassword = null
        String storePassword = null
        String keyAlias = null

        if (file.exists()) {
            def reader = KeystorePswRw.reader(file)
            keyPassword = reader.keyPass
            storePassword = reader.storePass
            keyAlias = reader.alias
        }

        if (StringsUtil.empty(keyPassword) ||
                StringsUtil.empty(storePassword) ||
                StringsUtil.empty(keyAlias)) {
            keyPassword = RandomPassword.gen()
            storePassword = RandomPassword.gen()
            keyAlias = RandomPassword.gen()
        }

        SigningConfig signingConfig = new SigningConfig("${SIGNING_CONFIG_NAME}-${name}")
        signingConfig.storeFile = project.file("${name}-${KEYSTORE_NAME}")
        signingConfig.keyPassword = keyPassword
        signingConfig.storePassword = storePassword
        signingConfig.keyAlias = keyAlias
        return signingConfig
    }

    private static void installGeneratorTask(Project project, ApplicationVariant variant, Task task) {
        if (task == null) {
            project.logger.lifecycle("gen ${variant.name} keystore task null")
            return
        }

        String name = variant.name
        String capName = name.capitalize()

        String generatorTaskName = "generat${capName}Keystore"

        GeneratorTask generatorTask = project.tasks.create(generatorTaskName, GeneratorTask)
        def storeFile = variant.signingConfig.storeFile
        generatorTask.keystoreFile = storeFile
        generatorTask.keystorePswFile = project.file("${storeFile.name}.psw")
        generatorTask.variant = variant

        task.dependsOn(generatorTask)
    }
}
