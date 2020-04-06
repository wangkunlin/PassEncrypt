package com.spoon.string.encrypt

import com.google.common.base.Charsets
import com.google.common.io.Closer
import com.squareup.javawriter.JavaWriter

import javax.lang.model.element.Modifier

import static com.google.common.base.Preconditions.checkNotNull

class StringDecryptGenerator {

    private static final Set<Modifier> PUBLIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.FINAL)
    private static final Set<Modifier> PUBLIC_STATIC = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC)
    private static final Set<Modifier> PUBLIC = EnumSet.of(Modifier.PUBLIC)
    private static final Set<Modifier> PRIVATE = EnumSet.of(Modifier.PRIVATE)
    private static final Set<Modifier> PRIVATE_STATIC_FINAL = EnumSet.of(Modifier.PRIVATE,
            Modifier.STATIC, Modifier.FINAL)

    private final File mGenFolder
    private final String mBuildConfigPackageName
    private final String mModuleName
    private final String mPsw

    StringDecryptGenerator(File genFolder, String buildConfigPackageName,
                           String moduleName, StringEncryptExtension extension) {
        mGenFolder = checkNotNull(genFolder)
        mBuildConfigPackageName = checkNotNull(buildConfigPackageName)
        String name = checkNotNull(moduleName)
        mModuleName = name.substring(0, 1).toUpperCase() + name.substring(1)
        mPsw = extension.password
    }

    private File getFolderPath() {
        return new File(mGenFolder, mBuildConfigPackageName.replace(".", File.separator))
    }

    void generate() {
        generateResources()
        generateDecrypt()
    }

    private void generateResources() {

        String typeName = "Wcg" + mModuleName + "Resources"
        String getter = "Wcg" + mModuleName + "String"
        File buildConfigJava = new File(getFolderPath(), typeName + ".java")

        Closer closer = Closer.create()
        try {
            FileOutputStream fos = closer.register(new FileOutputStream(buildConfigJava))
            OutputStreamWriter out = closer.register(new OutputStreamWriter(fos, Charsets.UTF_8))
            JavaWriter writer = closer.register(new JavaWriter(out))

            writer.emitJavadoc("Automatically generated file. DO NOT MODIFY")
                    .emitPackage(mBuildConfigPackageName)
                    .emitImports("android.content.res.Resources")
                    .emitEmptyLine()
                    .beginType(typeName, "class", PUBLIC_FINAL, "Resources")

            writer.emitEmptyLine()
            writer.emitField("Resources", "mRes", PRIVATE)

            writer.emitEmptyLine()

            writer.beginConstructor(PUBLIC, "Resources", "res")
            writer.emitStatement("super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration())")
            writer.emitStatement("mRes = res")
            writer.endConstructor()

            writer.emitEmptyLine()

            writer.beginMethod("String", "getString", PUBLIC, "int", "id")
            writer.emitStatement("return " + getter + ".getString(mRes, id)")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("String", "getString", PUBLIC,
                    "int", "id", "Object...", "formatArgs")
            writer.emitStatement("return " + getter + ".getString(mRes, id, formatArgs)")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("CharSequence", "getText", PUBLIC, "int", "id")
            writer.emitStatement("return " + getter + ".getText(mRes, id)")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("CharSequence", "getText", PUBLIC,
                    "int", "id", "CharSequence", "def")
            writer.emitStatement("return " + getter + ".getText(mRes, id, def)")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("CharSequence", "getQuantityText", PUBLIC,
                    "int", "id", "int", "quantity")
            writer.emitStatement("return " + getter + ".getQuantityText(mRes, id, quantity)")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("String", "getQuantityString", PUBLIC,
                    "int", "id", "int", "quantity")
            writer.emitStatement("return " + getter + ".getQuantityString(mRes, id, quantity)")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("String", "getQuantityString", PUBLIC,
                    "int", "id", "int", "quantity", "Object...", "formatArgs")
            writer.emitStatement("return " + getter + ".getQuantityString(mRes, id, quantity, formatArgs)")
            writer.endMethod()

            writer.emitEmptyLine()

            writer.endType()
        } catch (Throwable e) {
            throw closer.rethrow(e)
        } finally {
            closer.close()
        }
    }

    private void generateDecrypt() {

        String typeName = "Wcg" + mModuleName + "String"
        String resources = "Wcg" + mModuleName + "Resources"
        File buildConfigJava = new File(getFolderPath(), typeName + ".java")

        Closer closer = Closer.create()
        try {
            FileOutputStream fos = closer.register(new FileOutputStream(buildConfigJava))
            OutputStreamWriter out = closer.register(new OutputStreamWriter(fos, Charsets.UTF_8))
            JavaWriter writer = closer.register(new JavaWriter(out))

            writer.emitJavadoc("Automatically generated file. DO NOT MODIFY")
                    .emitPackage(mBuildConfigPackageName)
                    .emitImports("android.content.res.Resources",
                            "android.text.TextUtils",
                            mBuildConfigPackageName + ".R",
                            "com.spoon.pass.encrypt.EncryptDecrypt")
                    .emitEmptyLine()
                    .beginType(typeName, "class", PUBLIC_FINAL)

            writer.emitEmptyLine()

            writer.emitField("String", "key", PRIVATE_STATIC_FINAL, "\"" + mPsw + "\"")

            writer.emitEmptyLine()


            writer.beginMethod("String", "getString", PUBLIC_STATIC,
                    "Resources", "res", "int", "resId")

            writer.beginControlFlow("if (res instanceof " + resources + ")")
            writer.emitStatement("return res.getString(resId)")
            writer.endControlFlow()

            writer.emitStatement("String string = res.getString(resId)")
            writer.beginControlFlow("try")

            writer.emitStatement("String dec = EncryptDecrypt.decrypt(string, key)")
            writer.beginControlFlow("if (TextUtils.isEmpty(dec))")
            writer.emitStatement("return string")
            writer.endControlFlow()
            writer.emitStatement("return dec")

            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
//            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return string")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("String", "getString", PUBLIC_STATIC,
                    "Resources", "res", "int", "resId", "Object...", "formatArgs")

            writer.beginControlFlow("if (res instanceof " + resources + ")")
            writer.emitStatement("return res.getString(resId, formatArgs)")
            writer.endControlFlow()

            writer.emitStatement("String string = getString(res, resId)")
            writer.beginControlFlow("try")
            writer.emitStatement("return String.format(string, formatArgs)")
            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
//            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return string")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("CharSequence", "getText", PUBLIC_STATIC,
                    "Resources", "res", "int", "resId")

            writer.beginControlFlow("if (res instanceof " + resources + ")")
            writer.emitStatement("return res.getText(resId)")
            writer.endControlFlow()

            writer.emitStatement("CharSequence cs = res.getText(resId)")
            writer.beginControlFlow("try")

            writer.emitStatement("String dec = EncryptDecrypt.decrypt(cs.toString(), key)")
            writer.beginControlFlow("if (TextUtils.isEmpty(dec))")
            writer.emitStatement("return cs")
            writer.endControlFlow()
            writer.emitStatement("return dec")

            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
//            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return cs")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("CharSequence", "getText", PUBLIC_STATIC,
                    "Resources", "res", "int", "resId", "CharSequence", "def")

            writer.beginControlFlow("if (res instanceof " + resources + ")")
            writer.emitStatement("return res.getText(resId, def)")
            writer.endControlFlow()

            writer.emitStatement("CharSequence cs = res.getText(resId, def)")
            writer.beginControlFlow("try")

            writer.emitStatement("String dec = EncryptDecrypt.decrypt(cs.toString(), key)")
            writer.beginControlFlow("if (TextUtils.isEmpty(dec))")
            writer.emitStatement("return cs")
            writer.endControlFlow()
            writer.emitStatement("return dec")

            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
//            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return cs")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("CharSequence", "getQuantityText", PUBLIC_STATIC,
                    "Resources", "res", "int", "resId", "int", "quantity")

            writer.beginControlFlow("if (res instanceof " + resources + ")")
            writer.emitStatement("return res.getQuantityText(resId, quantity)")
            writer.endControlFlow()

            writer.emitStatement("CharSequence cs = res.getQuantityText(resId, quantity)")
            writer.beginControlFlow("try")

            writer.emitStatement("String dec = EncryptDecrypt.decrypt(cs.toString(), key)")
            writer.beginControlFlow("if (TextUtils.isEmpty(dec))")
            writer.emitStatement("return cs")
            writer.endControlFlow()
            writer.emitStatement("return dec")

            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
//            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return cs")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("String", "getQuantityString", PUBLIC_STATIC,
                    "Resources", "res", "int", "resId", "int", "quantity")

            writer.beginControlFlow("if (res instanceof " + resources + ")")
            writer.emitStatement("return res.getQuantityString(resId, quantity)")
            writer.endControlFlow()

            writer.emitStatement("String string = res.getQuantityString(resId, quantity)")
            writer.beginControlFlow("try")

            writer.emitStatement("String dec = EncryptDecrypt.decrypt(string, key)")
            writer.beginControlFlow("if (TextUtils.isEmpty(dec))")
            writer.emitStatement("return string")
            writer.endControlFlow()
            writer.emitStatement("return dec")

            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
//            writer.emitStatement("e.printStackTrace()")
            writer.endControlFlow()
            writer.emitStatement("return string")
            writer.endMethod()

            writer.emitEmptyLine()


            writer.beginMethod("String", "getQuantityString", PUBLIC_STATIC,
                    "Resources", "res", "int", "resId", "int", "quantity", "Object...", "formatArgs")

            writer.beginControlFlow("if (res instanceof " + resources + ")")
            writer.emitStatement("return res.getQuantityString(resId, quantity, formatArgs)")
            writer.endControlFlow()

            writer.emitStatement("String string = getQuantityString(res, resId, quantity)")
            writer.beginControlFlow("try")
            writer.emitStatement("return String.format(string, formatArgs)")
            writer.endControlFlow()
            writer.beginControlFlow("catch(Throwable e)")
//            writer.emitStatement("e.printStackTrace()")
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