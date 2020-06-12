package com.wcg.manifest.editor.run

import com.android.build.gradle.tasks.ManifestProcessorTask

class GradleCompat {

    static final String ANDROID_MANIFEST = "AndroidManifest.xml"

    static File getManifestOutputFile(ManifestProcessorTask task) {
        try {
            return get(task.getManifestOutputFile())
        } catch (Throwable e) {
            try {
                return new File(task.getManifestOutputDirectory().get().getAsFile(), ANDROID_MANIFEST)
            } catch (Throwable e2) {
                try {
                    return task.getManifestOutputFile()
                } catch (Throwable e3) {
                    return new File(task.getManifestOutputDirectory(), ANDROID_MANIFEST)
                }
            }
        }
    }

    private def static get(def provider) {
        return provider == null ? null : provider.get()
    }
}