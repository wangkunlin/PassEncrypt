package com.wcg.string.fog

import org.xxtea.XXTEA

import java.security.MessageDigest

/**
 * On 2020-06-12
 */
class Utils {

    static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5")
            md.update(s.getBytes())
            return new BigInteger(1, md.digest()).toString(16)
        } catch (Exception e) {
            return ""
        }
    }

    static String uniqueName(File fileInput) {
        final String fileInputName = fileInput.getName()
        if (fileInput.isDirectory()) {
            return fileInputName
        }
        final String parentDirPath = fileInput.parentFile.absolutePath
        final String pathMD5 = md5(parentDirPath)
        final int extSepPos = fileInputName.lastIndexOf('.')
        final String fileInputNamePrefix =
                (extSepPos >= 0 ? fileInputName.substring(0, extSepPos) : fileInputName)
        return fileInputNamePrefix + '_' + pathMD5
    }

    static void closeSafty(Closeable c) {
        try {
            c.close()
        } catch (Throwable e) {
            e.printStackTrace()
        }
    }

    static void copyStream(InputStream is, OutputStream os) {
        byte[] buffer = new byte[8192]
        int c
        while ((c = is.read(buffer)) != -1) {
            os.write(buffer, 0, c)
        }
    }

    static StringEnc enc(Object src, String password) {
        if (src == null) {
            return StringEnc.failed()
        }
        if (!(src instanceof String)) {
            return StringEnc.failed()
        }
        if (src.empty) {
            return StringEnc.failed()
        }
        try {
            String enc = XXTEA.encryptToBase64String(src, password)
            return StringEnc.success(enc, password)
        } catch (Throwable e) {
            e.printStackTrace()
            return StringEnc.failed()
        }
    }

}
