package com.spoon.string.encrypt
/**
 * Created by wangkunlin
 * On 2020-03-21
 */
class StringEncryptExtension {

    private String mInputFile

    private String mPassword

    String getInputFile() {
        return mInputFile
    }

    void inputFile(String inputFile) {
        mInputFile = inputFile
    }

    String getPassword() {
        return mPassword
    }

    void password(String password) {
        mPassword = password
    }
}
