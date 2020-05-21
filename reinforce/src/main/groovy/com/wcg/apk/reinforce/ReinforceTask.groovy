package com.wcg.apk.reinforce

import com.android.builder.model.SigningConfig
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import tencent.reinforce.lib.Reinforce
import util.CommonUtil

class ReinforceTask extends DefaultTask {

    private def variant

    @InputFile
    public File apkFile

    @OutputDirectory
    public File reinforceDir

    void setVariant(def variant) {
        this.variant = variant
    }

    @TaskAction
    void execute() {

        if (!variant.isSigningReady()) {
            throw new GradleException("${variant.name} sign not config")
        }

        ReinforceExtension extension = project.extensions.getByType(ReinforceExtension)

        SigningConfig signingConfig = variant.signingConfig

        project.logger.warn("To reinforce apk: ${apkFile.toString()}")

        deleteFile(reinforceDir)
        reinforceDir.mkdirs()

        def reinforce = new Reinforce.Builder(project.logger)
                .setDownloadPath(reinforceDir.getAbsolutePath())
                .setSid(extension.sid)
                .setSkey(extension.skey)
                .setUploadPath(apkFile.getAbsolutePath()).build()
        int result = reinforce.start()
        if (result != 0) {
            throw new GradleException("")
        }

        String reinforceApk = reinforce.getDownFilePath()

        String reinforceApkName = CommonUtil.getBaseName(new File(reinforceApk).getName())

        String sdkDir = project.android.sdkDirectory.absolutePath
        String buildToolsVersion = project.android.buildToolsVersion
        String buildToolsDir = "${sdkDir}${File.separator}build-tools${File.separator}${buildToolsVersion}"
        project.logger.warn("buildToolsDir: ${buildToolsDir}")

        String zipalignName = isWindows() ? "zipalign.exe" : "zipalign"
        String spksignerName = isWindows() ? "apksigner.bat" : "apksigner"

        String zipalign = "${buildToolsDir}${File.separator}${zipalignName}"
        String apksigner = "${buildToolsDir}${File.separator}${spksignerName}"

        String alignedApk = new File(reinforceDir, "${reinforceApkName}-aligned.apk")
        String alignedSignedApk = new File(reinforceDir, "${reinforceApkName}-aligned-signed.apk")

        String alignCmd = "${zipalign} 4 ${reinforceApk} ${alignedApk}"
        project.logger.warn("start align apk")
        project.logger.warn("zipalign cmd: ${alignCmd}")

        def runtime = Runtime.getRuntime()
        Process pro = runtime.exec(alignCmd)
        int status = pro.waitFor()
        BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()))

        String line
        while ((line = br.readLine()) != null) {
            project.logger.warn(line)
        }
        br.close()

        if (status != 0) {
            throw new GradleException("Failed to align apk")
        }
        project.logger.warn("aligend apk: ${alignedApk}")

        String signCmd = "${apksigner} sign -v --ks ${signingConfig.storeFile.absolutePath} " +
                "--ks-key-alias ${signingConfig.keyAlias} " +
                "--ks-pass pass:${signingConfig.storePassword} " +
                "--key-pass pass:${signingConfig.keyPassword} " +
                "--out ${alignedSignedApk} ${alignedApk}"
        project.logger.warn("start sign apk")
        project.logger.warn("sign apk cmd: ${signCmd}")

        pro = runtime.exec(signCmd)
        status = pro.waitFor()

        br = new BufferedReader(new InputStreamReader(pro.getInputStream()))

        while ((line = br.readLine()) != null) {
            project.logger.warn(line)
        }

        br.close()

        if (status != 0) {
            throw new GradleException("Failed to sign apk")
        }

        project.logger.warn("Aligend and Signed apk: ${alignedSignedApk}")

        project.logger.warn("Reinforce apk done.")
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows")
    }

    private static void deleteFile(File file) {
        if (!file.exists()) {
            return
        }
        if (file.isDirectory()) {
            def files = file.listFiles()
            for (def f : files) {
                deleteFile(f)
            }
        }
        file.delete()
    }
}
