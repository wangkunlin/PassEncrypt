package com.wcg.string.fog

/**
 * On 2020-09-03
 */
class StringEnc {
    boolean success
    String enc
    String psw

    StringEnc(boolean success, String enc, String psw) {
        this.success = success
        this.enc = enc
        this.psw = psw
    }

    static StringEnc success(String enc, String psw) {
        return new StringEnc(true, enc, psw)
    }

    static StringEnc failed() {
        return new StringEnc(false, null, null)
    }
}
