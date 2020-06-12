package com.wcg.manifest.editor.extension

import org.gradle.api.Action

/**
 * On 2020-06-12
 */
class ManifestExtension {

    private ApplicationConfig mApplicationConfig = new ApplicationConfig()

    void application(Action<ApplicationConfig> action) {
        action.execute(mApplicationConfig)
    }

    ApplicationConfig getApplication() {
        return mApplicationConfig
    }


    @Override
    String toString() {
        return "ManifestExtension{" +
                "mApplicationConfig=" + mApplicationConfig +
                '}'
    }
}
