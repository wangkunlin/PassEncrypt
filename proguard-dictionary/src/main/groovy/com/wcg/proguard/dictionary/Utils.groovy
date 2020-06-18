package com.wcg.proguard.dictionary

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

}
