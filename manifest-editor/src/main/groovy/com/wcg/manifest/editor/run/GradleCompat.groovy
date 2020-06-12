package com.wcg.manifest.editor.run

import com.android.build.gradle.tasks.ManifestProcessorTask

class GradleCompat {

    static final String ANDROID_MANIFEST = "AndroidManifest.xml"

    static List<File> getManifestOutputFile(ManifestProcessorTask task) {
        List<File> files = new ArrayList<>()
        getAppMainfest(task, files)
        getBundleManifest(task, files)
        return files
    }

    private static void getBundleManifest(ManifestProcessorTask task, List<File> files) {
        if (task.metaClass.respondsTo(task, "bundleManifestOutputDirectory") ||
                task.metaClass.hasProperty(task, "bundleManifestOutputDirectory") ||
                task.metaClass.respondsTo(task, "bundleManifestOutputFile") ||
                task.metaClass.hasProperty(task, "bundleManifestOutputFile")) {
            try {
                files.add(get(task.bundleManifestOutputFile))
            } catch (Throwable e) {
                try {
                    files.add(new File(task.bundleManifestOutputDirectory.get().asFile,
                            ANDROID_MANIFEST))
                } catch (Throwable e2) {
                    try {
                        files.add(task.bundleManifestOutputFile)
                    } catch (Throwable e3) {
                        files.add(new File(task.bundleManifestOutputDirectory, ANDROID_MANIFEST))
                    }
                }
            }

        }
    }

    private static void getAppMainfest(ManifestProcessorTask task, List<File> files) {
        try {
            files.add(get(task.manifestOutputFile))
        } catch (Throwable e) {
            try {
                files.add(new File(task.manifestOutputDirectory.get().asFile,
                        ANDROID_MANIFEST))
            } catch (Throwable e2) {
                try {
                    files.add(task.manifestOutputFile)
                } catch (Throwable e3) {
                    files.add(new File(task.manifestOutputDirectory, ANDROID_MANIFEST))
                }
            }
        }
    }

    private def static get(def provider) {
        return provider.get()
    }
}