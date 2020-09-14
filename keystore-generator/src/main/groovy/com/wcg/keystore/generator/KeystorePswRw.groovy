package com.wcg.keystore.generator

/**
 * On 2020-09-14
 */
class KeystorePswRw {

    private Properties mProperties

    static KeystorePswRw reader(File file) {
        Properties properties = new Properties()
        file.withInputStream {
            properties.load(it)
            it.close()
        }
        return new KeystorePswRw(properties)
    }

    static KeystorePswRw writer() {
        Properties properties = new Properties()
        return new KeystorePswRw(properties)
    }

    private KeystorePswRw(Properties properties) {
        mProperties = properties
    }

    String getStorePass() {
        return mProperties."store_pass"
    }

    void setStorePass(String storePass) {
        mProperties."store_pass" = storePass
    }

    String getKeyPass() {
        return mProperties."key_pass"
    }

    void setKeyPass(String keyPass) {
        mProperties."key_pass" = keyPass
    }

    String getAlias() {
        return mProperties."key_alias"
    }

    void setAlias(String keyAlias) {
        mProperties."key_alias" = keyAlias
    }

    void write(File file, String name) {
        file.withOutputStream {
            mProperties.store(it, "WARN: password info for ${name}, do not modify!")
            it.flush()
            it.close()
        }
    }

}
