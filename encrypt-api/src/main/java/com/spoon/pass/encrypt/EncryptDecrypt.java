package com.spoon.pass.encrypt;

import org.xxtea.XXTEA;

/**
 * Created by wangkunlin
 * On 2020-02-22
 */
public class EncryptDecrypt {

    public static String encrypt(String data, String key) {
        return XXTEA.encryptToBase64String(data, key);
    }

    public static String decrypt(String data, String key) {
        return XXTEA.decryptBase64StringToString(data, key);
    }
}
