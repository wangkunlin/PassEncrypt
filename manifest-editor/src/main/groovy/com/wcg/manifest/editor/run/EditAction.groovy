package com.wcg.manifest.editor.run

import com.android.build.gradle.tasks.ManifestProcessorTask
import com.wcg.manifest.editor.extension.ManifestExtension
import org.dom4j.QName
import org.dom4j.Namespace
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
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
            scanManifest(file, logger)
        }
    }

    private void scanManifest(File file, def logger) {
        logger.lifecycle("Found Manifest file {}", file.absolutePath)

        SAXReader reader = new SAXReader()
        def document = reader.read(file)

        def rootElement = document.rootElement

        def application = rootElement.element("application")

        def namespace = Namespace.get("android", ANDROID_NAMESPACE)

        mExtension.application.toRemove.each { remove ->
            logger.lifecycle("Remove config {}", remove)
            def nodes = application.elements(remove.what)

            nodes.each { node ->
                QName keyAttr = QName.get(remove.key, namespace)
                def value = node.attributeValue(keyAttr)
                if (value == remove.value) {
                    application.remove(node)
                    logger.lifecycle("Remove {}: {}", remove.what, remove.value)
                }
            }
        }

        logger.lifecycle("Scan AndroidManifest.xml completed")

        def outManifest = new File(file.parentFile, "EditManifest.xml")

        if (outManifest.exists()) {
            outManifest.delete()
        }

        OutputFormat format = OutputFormat.createPrettyPrint()
        format.indentSize = 4
        format.encoding = "utf-8"

        XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(outManifest), format)
        xmlWriter.write(document)
        xmlWriter.flush()
        xmlWriter.close()

        file.delete()

        outManifest.renameTo(file)
        logger.lifecycle("Rewrite AndroidManifest.xml")
    }
}
