package com.wcg.apk.reinforce

import com.android.builder.model.SigningConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tencent.reinforce.lib.Reinforce
import util.CommonUtil

class ReinforceTask extends DefaultTask {

    private def variant;

    void setVariant(def variant) {
        this.variant = variant
    }

    @TaskAction
    void execute() {

        if (!variant.isSigningReady()) {
            throw new IllegalStateException("${variant.name} sign not config")
        }

        ReinforceExtension extension = project.extensions.getByType(ReinforceExtension)

        SigningConfig signingConfig = variant.signingConfig

        def scope = variant.variantData.scope
        File apkDir = scope.apkLocation
        String apkName = variant.variantData.outputScope.mainSplit.outputFileName
        File apkFile = new File(apkDir, apkName)
        System.out.println(apkFile.toString())
        File reinforceDir = new File(apkDir, "reinforce")
        reinforceDir.mkdirs()
        def reinforce = new Reinforce.Builder()
                .setDownloadPath(reinforceDir.getAbsolutePath())
                .setSid(extension.sid)
                .setSkey(extension.skey)
                .setUploadPath(apkFile.getAbsolutePath()).build()
        reinforce.start()

        String reinforceApk = reinforce.getDownFilePath()

        String reinforceApkName = CommonUtil.getBaseName(new File(reinforceApk).getName())

        String sdkDir = project.android.sdkDirectory.absolutePath
        String buildToolsVersion = project.android.buildToolsVersion
        String buildToolsDir = "${sdkDir}${File.separator}build-tools${File.separator}${buildToolsVersion}"
        System.out.println("buildToolsDir: ${buildToolsDir}")

        String zipalignName = isWindows() ? "zipalign.exe" : "zipalign"
        String spksignerName = isWindows() ? "apksigner.bat" : "apksigner"

        String zipalign = "${buildToolsDir}${File.separator}${zipalignName}"
        String apksigner = "${buildToolsDir}${File.separator}${spksignerName}"

        String aligned = new File(reinforceDir, "${reinforceApkName}-aligned.apk")
        String alignedSigned = new File(reinforceDir, "${reinforceApkName}-aligned-signed.apk")

        String alignCmd = "${zipalign} 4 ${reinforceApk} ${aligned}"
        System.out.println("zipalign cmd: ${alignCmd}")

        def runtime = Runtime.getRuntime()
        System.out.println("start align apk")
        Process pro = runtime.exec(alignCmd)
        int status = pro.waitFor()
        BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()))

        String line
        while ((line = br.readLine()) != null) {
            System.out.println(line)
        }
        br.close()

        if (status != 0) {
            System.out.println("Failed to align apk")
            return
        }

        String signCmd = "${apksigner} sign -v --ks ${signingConfig.storeFile.absolutePath} " +
                "--ks-key-alias ${signingConfig.keyAlias} " +
                "--ks-pass pass:${signingConfig.storePassword} " +
                "--key-pass pass:${signingConfig.keyPassword} " +
                "--out ${alignedSigned} ${aligned}"
        System.out.println("sign apk cmd: ${signCmd}")

        System.out.println("start sign apk")
        pro = runtime.exec(signCmd)
        status = pro.waitFor()

        br = new BufferedReader(new InputStreamReader(pro.getInputStream()))

        while ((line = br.readLine()) != null) {
            System.out.println(line)
        }

        br.close()

        if (status != 0) {
            System.out.println("Failed to sign apk")
        } else {
            System.out.println("Reinforce apk done.")
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows")
    }
}
