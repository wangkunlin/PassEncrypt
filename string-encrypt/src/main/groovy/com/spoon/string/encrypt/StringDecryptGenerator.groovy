package com.spoon.string.encrypt

import com.google.common.base.Charsets
import com.google.common.io.Closer
import com.squareup.javawriter.JavaWriter

import javax.lang.model.element.Modifier

import static com.google.common.base.Preconditions.checkNotNull

class StringDecryptGenerator {

    private static final Set<Modifier> PUBLIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.FINAL)
    private static final Set<Modifier> PUBLIC_STATIC = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC)

    private final File mGenFolder
    private final String mBuildConfigPackageName
    private final String mModuleName
    private final StringEncryptExtension mExtension
    private final String mResId

    StringDecryptGenerator(File genFolder, String buildConfigPackageName,
                           String moduleName, StringEncryptExtension extension,
                           String resId) {
        mGenFolder = checkNotNull(genFolder)
        mBuildConfigPackageName = checkNotNull(buildConfigPackageName)
        String name = checkNotNull(moduleName)
        mModuleName = name.substring(0, 1).toUpperCase() + name.substring(1)
        mExtension = checkNotNull(extension)
        mResId = checkNotNull(resId)
    }

    private File getFolderPath() {
        return new File(mGenFolder, mBuildConfigPackageName.replace(".", File.separator))
    }

    void generate() {

        String typeName = mModuleName + "StringGetter"
        File buildConfigJava = new File(getFolderPath(), typeName + ".java")

        Closer closer = Closer.create()
        try {
            FileOutputStream fos = closer.register(new FileOutputStream(buildConfigJava))
            OutputStreamWriter out = closer.register(new OutputStreamWriter(fos, Charsets.UTF_8))
            JavaWriter writer = closer.register(new JavaWriter(out))

            writer.emitJavadoc("Automatically generated file. DO NOT MODIFY")
                    .emitPackage(mBuildConfigPackageName)
                    .emitImports("android.content.Context", mBuildConfigPackageName + ".R",
                            "com.spoon.pass.encrypt.EncryptDecrypt")
                    .emitEmptyLine()
                    .beginType(typeName, "class", PUBLIC_FINAL)

            writer.emitEmptyLine()


            writer.beginMethod("String", "getString", PUBLIC_STATIC,
                    "Context", "c", "int", "resId")
            writer.emitStatement("String string = c.getString(resId)")
            writer.emitStatement("String key = c.getString(R.string." + mResId + ")")
            writer.beginControlFlow("try")
            writer.emitStatement("return EncryptDecrypt.decrypt(string, key)")
            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return string")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("String", "getString", PUBLIC_STATIC,
                    "Context", "c", "int", "resId", "Object...", "formatArgs")
            writer.emitStatement("String string = getString(c, resId)")
            writer.beginControlFlow("try")
            writer.emitStatement("return String.format(string, formatArgs)")
            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return string")
            writer.endMethod()

            writer.emitEmptyLine()

            writer.endType()
        } catch (Throwable e) {
            throw closer.rethrow(e)
        } finally {
            closer.close()
        }
    }
}