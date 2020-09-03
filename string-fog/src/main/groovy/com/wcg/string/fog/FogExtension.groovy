package com.wcg.string.fog

/**
 * On 2020-09-02
 */
class FogExtension {
    Boolean enable

    String password // if not set, will use random string

    String[] packages = []

    boolean enable() {
        return enable != null && enable
    }
}
