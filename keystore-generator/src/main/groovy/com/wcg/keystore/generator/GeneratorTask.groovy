package com.wcg.keystore.generator

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.core.BuilderConstants
import com.android.builder.model.SigningConfig
import com.android.builder.signing.DefaultSigningConfig
import com.android.ide.common.signing.KeystoreHelper
import com.android.utils.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.security.KeyStore

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
        if (isDebug(signingConfig)) {
            return
        }
        if (!keystorePswFile.exists()) {
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

    // copy from ValidateSigningTask.isSigningConfigUsingTheDefaultDebugKeystore()
    private static boolean isDebug(SigningConfig signingConfig) {
        return signingConfig.name == BuilderConstants.DEBUG &&
                signingConfig.keyAlias == DefaultSigningConfig.DEFAULT_ALIAS &&
                signingConfig.keyPassword == DefaultSigningConfig.DEFAULT_PASSWORD &&
                signingConfig.storePassword == DefaultSigningConfig.DEFAULT_PASSWORD &&
                signingConfig.storeType == KeyStore.getDefaultType() &&
                FileUtils.isSameFile(signingConfig.storeFile, new File(KeystoreHelper.defaultDebugKeystoreLocation()))
    }

}
