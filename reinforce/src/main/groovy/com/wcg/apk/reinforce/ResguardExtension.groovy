package com.wcg.apk.reinforce

import org.gradle.api.Named

/**
 * On 2020-07-25
 */
class ResguardExtension implements Named {

    private final String name
    String config
    boolean enabled = true

    ResguardExtension(String name) {
        this.name = name
    }

    @Override
    String getName() {
        return name
    }

    String getConfig() {
        return config
    }

    void config(String config) {
        this.config = config
    }

    boolean getEnabled() {
        return enabled
    }

    void enabled(boolean enabled) {
        this.enabled = enabled
    }
}
