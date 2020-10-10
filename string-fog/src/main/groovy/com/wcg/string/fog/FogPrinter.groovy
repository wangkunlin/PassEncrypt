package com.wcg.string.fog

import com.android.utils.FileUtils

/**
 * On 2020-09-05
 */
class FogPrinter implements Closeable {

    private File mFile
    private BufferedWriter mWriter
    private String mCurClass
    private List<String> mMapping = new LinkedList<>()
    private List<Integer> mToRemove = new ArrayList<>()

    FogPrinter(File file) {
        mFile = file
    }

    private void ensure() {
        if (mWriter != null) {
            return
        }
        FileUtils.mkdirs(mFile.parentFile)

        if (mFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(mFile))
            String line
            while ((line = reader.readLine()) != null) {
                mMapping.add(line)
            }
            reader.close()
        }

        mWriter = new BufferedWriter(new FileWriter(mFile))
    }

    void print(String className, String origin, String encrypted, String psw) {
        if (className == null) {
            className = ""
        }
        ensure()
        if (mCurClass != className) {
            if (!mMapping.empty) {
                mMapping.add("")
            }
            String clazzLine = "class: ${className.replace('/', '.')}"
            int index = mMapping.indexOf(clazzLine)
            if (index >= 0) { // increase compile
                mToRemove.add(index)
            }
            mMapping.add(clazzLine)
            mCurClass = className
        }
        mMapping.add("${origin} + ${psw} --> ${encrypted}")
    }

    @Override
    void close() throws IOException {
        writeMapping()
        if (mWriter != null) {
            mWriter.flush()
            mWriter.close()
        }
    }

    private void writeMapping() {
        int n = mMapping.size()
        boolean remove = false
        for (int i = 0; i < n; i++) {
            String line = mMapping.get(i)
            if (mToRemove.contains(i)) { // need remove
                remove = true
            }
            if (remove) {
                if (line.trim().empty) { // empty line, means class record end
                    remove = false
                }
            } else {
                if (mWriter != null) {
                    mWriter.write(line)
                    mWriter.newLine()
                }
            }
        }
    }
}
