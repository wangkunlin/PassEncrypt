package com.wcg.manifest.editor.run

import com.android.build.gradle.tasks.ManifestProcessorTask
import com.wcg.manifest.editor.extension.ManifestExtension
import groovy.xml.QName
import org.gradle.api.Action

/**
 * On 2020-06-12
 */
class EditAction implements Action<ManifestProcessorTask> {

    private ManifestExtension mExtension
    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"

    EditAction(ManifestExtension extension) {
        mExtension = extension
    }

    @Override
    void execute(ManifestProcessorTask task) {
        def files = GradleCompat.getManifestOutputFile(task)
        def logger = task.project.logger
        files.each { file ->
            logger.lifecycle("get manifest file: ${file}")
            scanManifest(file, logger)
        }
    }

    private void scanManifest(File file, def logger) {
        logger.lifecycle("Found Manifest file {}", file.absolutePath)

        def manifest = new XmlParser().parse(file)

        def application = manifest.application[0]

        mExtension.application.toRemove.each { remove ->
            QName qName = new QName(ANDROID_NAMESPACE, remove.key)
            application."${remove.what}"
                    .findAll { it.attribute(qName) == remove.value }
                    .each {
                        logger.lifecycle("Remove config ${remove}")
                        application.remove(it)
                    }
        }

        logger.lifecycle("Scan AndroidManifest.xml completed")

        def outManifest = new File(file.parentFile, "EditManifest.xml")

        if (outManifest.exists()) {
            outManifest.delete()
        }

        PrintWriter xmlWriter = new PrintWriter(new FileOutputStream(outManifest))

        XmlNodePrinter printer = new XmlNodePrinter(xmlWriter, '    ', '"')
        printer.print(manifest)

        xmlWriter.flush()
        xmlWriter.close()

        file.delete()

        outManifest.renameTo(file)
        logger.lifecycle("Rewrite AndroidManifest.xml")
    }
}
