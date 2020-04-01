package com.spoon.string.encrypt

import com.google.common.collect.Lists
import com.spoon.pass.encrypt.EncryptDecrypt
import org.w3c.dom.*

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class StringLoader {

    private final File mCurFolder
    private final StringEncryptExtension mExtension

    StringLoader(File curFolder, StringEncryptExtension extension) {
        mCurFolder = curFolder
        mExtension = extension
    }

    List<ResString> load() {
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

        String password = mExtension.password

        for (int i = 0; i < childNodes.length; ++i) {
            Node child = childNodes.item(i)
            if (child instanceof Element) {
                String tagName = child.getTagName()
                if (tagName == null || tagName.isEmpty() || tagName != "string") {
                    // 过滤掉 非 string 标签的元素, string-array 暂不支持
                    continue
                }
                Attr nameAttr = (Attr) child.getAttributes().getNamedItem("name")
                String name = nameAttr.value
                String value = child.getFirstChild().textContent

                Attr keepAttr = (Attr) child.getAttributes().getNamedItem("keep")
                boolean keep = false
                if (keepAttr != null) {
                    keep = Boolean.parseBoolean(keepAttr.value)
                }

                if (!keep) {
                    value = EncryptDecrypt.encrypt(value, password)
                }

                items.add(ResString.string(name, value))
            }
        }
        return items
    }
}