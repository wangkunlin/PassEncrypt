package com.wcg.proguard.dictionary

import com.google.common.io.Files
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import java.nio.charset.StandardCharsets

/**
 * On 2020-06-17
 */
class GenerateProguardDictionaryTask extends DefaultTask {

    public File proguardFile
    public File dictionaryFile
    public Task proguardTask

    @TaskAction
    void action() {
        deleteFile(proguardFile)
        deleteFile(dictionaryFile)

        Files.createParentDirs(proguardFile)

        StringBuilder sb = new StringBuilder()
        sb.append("-obfuscationdictionary ").append(dictionaryFile.name).append('\n')
        sb.append("-classobfuscationdictionary ").append(dictionaryFile.name).append('\n')
        sb.append("-packageobfuscationdictionary ").append(dictionaryFile.name).append('\n')

        Files.asCharSink(proguardFile, StandardCharsets.UTF_8).write(sb.toString())

        DictionaryExtension extension = project.extensions.findByName("dictionary")
        if (extension == null) {
            extension = new DictionaryExtension()
        }

        RandomChars generator = new RandomChars(extension.fromChars)
        sb = new StringBuilder()

        int count = extension.constraintedCount()

        for (int i = 0; i < count; i++) {
            sb.append(generator.generate()).append('\n')
        }

        String content = sb.toString()

        Files.asCharSink(dictionaryFile, StandardCharsets.UTF_8).write(content)

        proguardTask.inputs.property("proguard-dictironary", Utils.md5(content))
    }

    private static void deleteFile(File file) {
        if (file.exists()) {
            if (!file.delete()) {
                throw new IllegalStateException("Fail to delete file ${file}")
            }
        }
    }
}
