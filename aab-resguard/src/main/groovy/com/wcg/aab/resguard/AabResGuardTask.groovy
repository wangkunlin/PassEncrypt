package com.wcg.aab.resguard

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.model.SigningConfig
import com.bytedance.android.aabresguard.commands.ObfuscateBundleCommand
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path
/**
 * On 2020-10-22
 */
class AabResGuardTask extends DefaultTask {

    AabResGuardTask() {
        description = "Assemble resource proguard for bundle file"
        group = "bundle"
        outputs.upToDateWhen { false }
    }

    ApplicationVariant variant
    Path bundlePath
    Path obfuscatedBundlePath
    AabResGuardExtension aabResGuard
    SigningConfig signingConfig

    void setVariant(ApplicationVariant variant) {
        this.variant = variant
        bundlePath = BundleUtil.getBundleFilePath(project, variant.name.capitalize())
        aabResGuard = project.aabResGuard
        obfuscatedBundlePath = new File(bundlePath.toFile().parentFile,
                aabResGuard.obfuscatedBundleFileName).toPath()
    }

    @TaskAction
    void taskAction() {
        prepareUnusedFile()

        def command =
                ObfuscateBundleCommand.builder()
                        .setEnableObfuscate(aabResGuard.enableObfuscate)
                        .setBundlePath(bundlePath)
                        .setOutputPath(obfuscatedBundlePath)
                        .setMergeDuplicatedResources(aabResGuard.mergeDuplicatedRes)
                        .setWhiteList(aabResGuard.whiteList)
                        .setFilterFile(aabResGuard.enableFilterFiles)
                        .setFileFilterRules(aabResGuard.filterList)
                        .setRemoveStr(aabResGuard.enableFilterStrings)
                        .setUnusedStrPath(aabResGuard.unusedStringPath)
                        .setLanguageWhiteList(aabResGuard.languageWhiteList)
        if (aabResGuard.mappingFile != null) {
            command.setMappingPath(aabResGuard.mappingFile)
        }

        if (variant.signingReady) {
            command.setStoreFile(signingConfig.storeFile.toPath())
                    .setKeyAlias(signingConfig.keyAlias)
                    .setKeyPassword(signingConfig.keyPassword)
                    .setStorePassword(signingConfig.storePassword)
        }
        command.build().execute()
    }

    void prepareUnusedFile() {
        def resourcePath = "${project.buildDir}/outputs/mapping/${variant.name}/unused.txt"
        def usedFile = new File(resourcePath)
        if (usedFile.exists()) {
            println("find unused.txt : ${usedFile.absolutePath}")
            if (aabResGuard.enableFilterStrings) {
                if (aabResGuard.unusedStringPath == null || aabResGuard.unusedStringPath.empty) {
                    aabResGuard.unusedStringPath = usedFile.absolutePath
                    println("replace unused.txt!")
                }
            }
        } else {
            println("not exists unused.txt : ${usedFile.absolutePath}\n" +
                    "use default path : ${aabResGuard.unusedStringPath}")
        }
    }
}
