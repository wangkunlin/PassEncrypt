package com.wcg.string.fog.utils
/**
 * On 2020-10-10
 */
class EncryptString {

    private static String sPsw
    private PasswordGenerator mPasswordGenerator

    private EncryptString() {
        mPasswordGenerator = new PasswordGenerator(sPsw)
    }

    static void setPassword(String password) {
        sPsw = password
    }

    static EncryptString getInstance() {
        return Holder.sInstance
    }

    private static class Holder {
        private static EncryptString sInstance = new EncryptString()
    }

    private Map<Object, StringEnc> mEnc = new HashMap<>()

    StringEnc enc(Object source) {
        StringEnc stringEnc = mEnc.get(source)
        if (stringEnc != null) {
            return stringEnc
        }
        stringEnc = Utils.enc(source, mPasswordGenerator.gen())
        mEnc.put(source, stringEnc)
        return stringEnc
    }

}
