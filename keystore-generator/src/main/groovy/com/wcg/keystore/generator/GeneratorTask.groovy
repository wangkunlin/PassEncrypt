package com.wcg.keystore.generator

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.core.BuilderConstants
import com.android.builder.model.SigningConfig
import com.android.ide.common.signing.KeystoreHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * On 2020-07-25
 */
class GeneratorTask extends DefaultTask {

    @OutputFile
    File keystoreFile

    @OutputFile
    File keystorePswFile

    ApplicationVariant variant

    @TaskAction
    void taskAction() {
        SigningConfig signingConfig = variant.signingConfig
        if (!keystorePswFile.exists() && signingConfig.name != BuilderConstants.DEBUG) {
            keystorePswFile.createNewFile()

            def pswRw = KeystorePswRw.writer()
            pswRw.alias = signingConfig.keyAlias
            pswRw.storePass = signingConfig.storePassword
            pswRw.keyPass = signingConfig.keyPassword
            pswRw.write(keystorePswFile, keystoreFile.name)
        }
        if (keystoreFile != null && keystoreFile.exists()) {
            return
        }
        keystoreFile = signingConfig.storeFile

        if (keystoreFile != null && keystoreFile.exists()) {
            return
        }

        try {
            KeystoreHelper.createNewStore(signingConfig.storeType,
                    keystoreFile, signingConfig.storePassword, signingConfig.keyPassword,
                    signingConfig.keyAlias, "CN=Android,O=Android,C=US", 30)
        } catch (Throwable e) {
            keystoreFile.delete()
            throw e
        }

    }

}
