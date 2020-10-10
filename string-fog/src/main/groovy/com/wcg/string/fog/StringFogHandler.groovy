package com.wcg.string.fog

import com.wcg.string.fog.asm.FogClassVisitor
import com.wcg.string.fog.utils.FogLogger
import com.wcg.string.fog.utils.Utils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * On 2020-09-02
 */
class StringFogHandler {
    private FogExtension mExtension
    private FogLogger mLogger
    private FogPrinter mPrinter

    StringFogHandler(FogExtension extension, FogLogger logger, FogPrinter printer) {
        mExtension = extension
        mLogger = logger
        mPrinter = printer
    }

    void fogClass(File fileIn, File fileOut) {
        InputStream is = null
        OutputStream os = null
        try {
            is = new BufferedInputStream(new FileInputStream(fileIn))
            os = new BufferedOutputStream(new FileOutputStream(fileOut))
            handleClass(is, os)
        } finally {
            Utils.closeSafty(is)
            Utils.closeSafty(os)
        }
    }

    void fogJar(File jarIn, File jarOut) {
        try {
            processJar(jarIn, jarOut, StandardCharsets.UTF_8, StandardCharsets.UTF_8)
        } catch (IllegalArgumentException e) {
            if ("MALFORMED" == e.getMessage()) {
                processJar(jarIn, jarOut, Charset.forName("GBK"), StandardCharsets.UTF_8)
            } else {
                throw e
            }
        }
    }

    private void processJar(File jarIn, File jarOut, Charset charsetIn,
                            Charset charsetOut) throws IOException {
        ZipInputStream zis = null
        ZipOutputStream zos = null
        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(jarIn)), charsetIn)
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(jarOut)), charsetOut)
            ZipEntry entryIn
            Map<String, Integer> processedEntryNamesMap = new HashMap<>()
            while ((entryIn = zis.getNextEntry()) != null) {
                final String entryName = entryIn.name
                if (!processedEntryNamesMap.containsKey(entryName)) {
                    ZipEntry entryOut = new ZipEntry(entryIn)
                    // Set compress method to default
                    if (entryOut.method != ZipEntry.DEFLATED) {
                        entryOut.method = ZipEntry.DEFLATED
                    }
                    entryOut.compressedSize = -1
                    zos.putNextEntry(entryOut)
                    if (!entryIn.directory) {
                        if (entryName.endsWith(".class")) {
                            handleClass(zis, zos)
                        } else {
                            Utils.copyStream(zis, zos)
                        }
                    }
                    zos.closeEntry()
                    processedEntryNamesMap.put(entryName, 1)
                }
            }
        } finally {
            Utils.closeSafty(zos)
            Utils.closeSafty(zis)
        }
    }

    private void handleClass(InputStream is, OutputStream os) {
        ClassReader cr = new ClassReader(is)
        // skip module-info class
        if ("module-info" == cr.className) {
            Utils.copyStream(is, os)
        } else {
            ClassWriter cw = new ClassWriter(0)
            ClassVisitor cv
            if (shouldFog(mExtension.packages, cr.className)) {
                mLogger.lifecycle("fog class: ${cr.className}")
                cv = new FogClassVisitor(cw, mLogger, mPrinter)
            } else {
                cv = cw
            }
            cr.accept(cv, 0)
            os.write(cw.toByteArray())
        }

        os.flush()
    }

    private static boolean shouldFog(String[] packages, String className) {
        if (packages == null || packages.length == 0) {
            return false
        }
        if (className == null || className.empty) {
            return false
        }
        String realClassName = className.replace('/', '.')
        for (String pkg : packages) {
            if (realClassName.startsWith(pkg + ".")) {
                return true
            }
        }
        return false
    }
}
