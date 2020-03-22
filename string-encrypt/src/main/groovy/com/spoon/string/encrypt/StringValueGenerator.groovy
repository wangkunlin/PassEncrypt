package com.spoon.string.encrypt

import com.android.ide.common.xml.XmlPrettyPrinter
import com.android.utils.XmlUtils
import com.google.common.base.Charsets
import com.google.common.collect.Lists
import com.google.common.io.Files
import com.spoon.pass.encrypt.EncryptDecrypt
import org.w3c.dom.*

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Class able to generate a res value file in an Android project.
 */
class StringValueGenerator {

    private static final String RES_VALUE_FILENAME_XML = "encryptedStringValues.xml"

    private final File mGenFolder
    private final File mCurFolder
    private final String mResId
    private final StringEncryptExtension mExtension

    /**
     * Creates a generator
     * @param genFolder the gen folder of the project
     */
    StringValueGenerator(File curFolder, File genFolder, String resId, StringEncryptExtension extension) {
        mCurFolder = checkNotNull(curFolder)
        mGenFolder = checkNotNull(genFolder)
        mResId = checkNotNull(resId)
        mExtension = checkNotNull(extension)
    }

    private List<ResString> readStrings() {
        List<ResString> items = Lists.newArrayList()
        File file = new File(mCurFolder, mExtension.inputFile)

        try {
            file = file.canonicalFile
        } catch (Throwable e) {
            e.printStackTrace()
            file = file.absoluteFile
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.setNamespaceAware(true)
        factory.setValidating(false)
        factory.setIgnoringComments(true)
        DocumentBuilder builder = factory.newDocumentBuilder()
        Document document = builder.parse(file)

        Node rootNode = document.getElementsByTagName("resources").item(0)
        NodeList childNodes = rootNode.getChildNodes()

        if (childNodes == null || childNodes.length == 0) {
            return items
        }

        items.add(ResString.password(mResId, mExtension.password))

        for (int i = 0; i < childNodes.length; ++i) {
            Node child = childNodes.item(i)
            if (child instanceof Element) {
                Attr nameAttr = (Attr) child.getAttributes().getNamedItem("name")
                String name = nameAttr.value
                String value = child.getFirstChild().textContent

                Attr keepAttr = (Attr) child.getAttributes().getNamedItem("keep")
                boolean keep = false
                if (keepAttr != null) {
                    keep = Boolean.parseBoolean(keepAttr.value)
                }

                items.add(ResString.string(name, value, keep))
            }
        }

        return items
    }
    /**
     * Returns a File representing where the BuildConfig class will be.
     */
    File getFolderPath() {
        // 应该是 可以支持 多语言
        return new File(mGenFolder, "values")
    }

    /**
     * Generates the resource files
     */
    void generate() throws IOException, ParserConfigurationException {
        File pkgFolder = getFolderPath()
        if (!pkgFolder.isDirectory()) {
            if (!pkgFolder.mkdirs()) {
                throw new RuntimeException("Failed to create " + pkgFolder.getAbsolutePath())
            }
        }

        List<ResString> items = readStrings()
        if (items.isEmpty() || items.size() == 1) {
            return
        }

        File resFile = new File(pkgFolder, RES_VALUE_FILENAME_XML)

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.setNamespaceAware(true)
        factory.setValidating(false)
        factory.setIgnoringComments(true)
        DocumentBuilder builder

        builder = factory.newDocumentBuilder()
        Document document = builder.newDocument()

        Node rootNode = document.createElement("resources")
        document.appendChild(rootNode)

        rootNode.appendChild(document.createTextNode("\n"))
        rootNode.appendChild(document.createComment("Automatically generated file. DO NOT MODIFY"))
        rootNode.appendChild(document.createTextNode("\n\n"))

        for (ResString field : items) {

            Node itemNode = document.createElement("string")
            Attr nameAttr = document.createAttribute("name")

            nameAttr.setValue(field.name)
            itemNode.getAttributes().setNamedItem(nameAttr)

            Attr translatable = document.createAttribute("translatable")
            translatable.setValue("false")
            itemNode.getAttributes().setNamedItem(translatable)

            if (field.value != null && !field.value.isEmpty()) {

                String value
                if (field.keep) {
                    value = field.value
                } else {
                    value = EncryptDecrypt.encrypt(field.value, mExtension.password)
                }
                itemNode.appendChild(document.createTextNode(value))
            }

            rootNode.appendChild(itemNode)
        }

        String content
        try {
            content = XmlPrettyPrinter.prettyPrint(document, true)
        } catch (Throwable t) {
            content = XmlUtils.toXml(document)
        }

        Files.asCharSink(resFile, Charsets.UTF_8).write(content)
    }
}
