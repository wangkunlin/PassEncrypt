package com.wcg.apk.reinforce

import org.gradle.api.Named

/**
 * On 2020-07-25
 */
class ResguardExtension implements Named {

    private final String name
    String config
    Boolean enable

    ResguardExtension(String name) {
        this.name = name
    }

    @Override
    String getName() {
        return name
    }

    void config(String config) {
        this.config = config
    }

    void enable(boolean enable) {
        this.enable = enable
    }

    boolean disabled() {
        return config == null || enable == null || !enable
    }
}
