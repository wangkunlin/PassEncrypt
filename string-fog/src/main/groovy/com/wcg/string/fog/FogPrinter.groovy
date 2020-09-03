package com.wcg.string.fog

import com.android.utils.FileUtils

/**
 * On 2020-09-05
 */
class FogPrinter implements Closeable {

    private File mFile
    private BufferedWriter mWriter
    private String mCurClass

    FogPrinter(File file) {
        mFile = file
    }

    private void ensure() {
        if (mWriter != null) {
            return
        }
        FileUtils.mkdirs(mFile.parentFile)
        FileUtils.deleteIfExists(mFile)
        mWriter = new BufferedWriter(new FileWriter(mFile))
    }

    void print(String className, String origin, String encrypted, String psw) {
        if (className == null) {
            className = ""
        }
        ensure()
        if (mCurClass != className) {
            mWriter.newLine()
            mWriter.write("class: ${className.replace('/', '.')}")
            mWriter.newLine()
            mCurClass = className
        }
        mWriter.write("${origin} --\"${psw}\"--> ${encrypted}")
        mWriter.newLine()
    }

    @Override
    void close() throws IOException {
        if (mWriter != null) {
            mWriter.flush()
            mWriter.close()
        }
    }
}
