package com.wcg.string.fog

/**
 * On 2020-09-02
 */
class FogExtension {
    Boolean enable

    String password // if not set, will use random string

    String[] packages = []

    Boolean debugEnable

    boolean enabled() {
        return enable != null && enable && packages != null && packages.length > 0
    }

    Boolean printLog

    boolean log() {
        return printLog != null && printLog
    }

    boolean debugEnabled() {
        return enabled() && debugEnable != null && debugEnable
    }
}
