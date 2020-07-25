package com.wcg.apk.reinforce

import org.gradle.api.Named

/**
 * On 2020-07-25
 */
class ResguardExtension implements Named {

    private final String name
    String config

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
}
