package com.wcg.apk.reinforce

import apksigner.ApkSignerTool
import com.android.builder.model.SigningConfig
import com.tencent.mm.resourceproguard.cli.CliMain
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

        ReinforceExtension extension = project.extensions.getByType(ReinforceExtension)
        if (extension.disable != null && extension.disable && resguardDisabled()) {
            project.logger.lifecycle("Reinforce: Skip run")
            return
        }

        if (!variant.isSigningReady()) {
            throw new GradleException("${variant.name} sign not config")
        }

        SigningConfig signingConfig = variant.signingConfig
        int minSdkVersion = variant.mergedFlavor.minSdkVersion.apiLevel

        project.logger.lifecycle("To reinforce apk: ${apkFile.toString()}")

        deleteFile(reinforceDir)
        reinforceDir.mkdirs()

        // step 1 resguard if needed
        String resguardApkPath = reguard(signingConfig,
                extension.disable != null && extension.disable,
                reinforceDir.absolutePath)
        if (resguardApkPath == null) {
            resguardApkPath = apkFile.absolutePath
        }

        // step 2 reinforce
        String reinforceApk = reinforce(extension, resguardApkPath)

        // step 3 4k align apk
        String reinforceApkName = CommonUtil.getBaseName(new File(reinforceApk).name)
        String alignedApk = new File(reinforceDir, "${reinforceApkName}-aligned.apk")
        aligneApk(reinforceApk, alignedApk)

        // step 4 resign apk
        String alignedSignedApk = new File(reinforceDir, "${reinforceApkName}-aligned-signed.apk")
        signApk(alignedApk, alignedSignedApk, minSdkVersion, signingConfig)

        project.logger.lifecycle("Aligend and Signed apk: ${alignedSignedApk}")

        project.logger.lifecycle("Reinforce apk done.")
    }

    private void aligneApk(String inApk, String outApk) {

        String sdkDir = project.android.sdkDirectory.absolutePath
        String buildToolsVersion = project.android.buildToolsVersion
        String buildToolsDir = "${sdkDir}${File.separator}build-tools${File.separator}${buildToolsVersion}"
        project.logger.lifecycle("buildToolsDir: ${buildToolsDir}")

        String zipalignName = isWindows() ? "zipalign.exe" : "zipalign"

        String zipalign = "${buildToolsDir}${File.separator}${zipalignName}"


        String alignCmd = "${zipalign} 4 ${inApk} ${outApk}"
        project.logger.lifecycle("start align apk")
        project.logger.lifecycle("zipalign cmd: ${alignCmd}")

        def runtime = Runtime.getRuntime()
        Process pro = runtime.exec(alignCmd)
        int status = pro.waitFor()
        BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()))

        String line
        while ((line = br.readLine()) != null) {
            project.logger.lifecycle(line)
        }
        br.close()

        if (status != 0) {
            throw new GradleException("Failed to align apk, exit code: ${status}")
        }
        project.logger.lifecycle("aligend apk: ${outApk}")
    }

    private String reinforce(ReinforceExtension extension, String inApk) {
        if (extension.disable != null && extension.disable) {
            return inApk
        }
        def reinforce = new Reinforce.Builder(project.logger)
                .setDownloadPath(reinforceDir.absolutePath)
                .setSid(extension.sid)
                .setSkey(extension.skey)
                .setUploadPath(inApk).build()
        int result = reinforce.start()
        if (result != 0) {
            throw new GradleException("")
        }
        return reinforce.getDownFilePath()
    }

    private boolean resguardDisabled() {
        ResguardExtension resguard = project.extensions.findByName("resguard") as ResguardExtension

        if (resguard == null || (resguard.disable != null && resguard.disable) || resguard.config == null) {
            return true
        }
        File file = project.file(resguard.config)
        if (file == null || !file.exists()) {
            return true
        }
        return false
    }

    private String reguard(SigningConfig signingConfig, boolean disable, String reinforceDir) {
        ResguardExtension resguard = project.extensions.findByName("resguard") as ResguardExtension

        if (resguardDisabled()) {
            return apkFile.absolutePath
        }

        File file = project.file(resguard.config)
        project.logger.lifecycle("Resguard config file: ${file.absolutePath}")
        if (!file.exists()) {
            return apkFile.absolutePath
        }

        File resguardDir = new File(reinforceDir, "resguard")
        resguardDir.mkdirs()

        String fileName = CommonUtil.getBaseName(apkFile.name)

        File resguardApkFile = new File(reinforceDir, "${fileName}-resguard.apk")

        List<String> params = new ArrayList<>()
        params.add(apkFile.absolutePath)
        params.add("-config")
        params.add(file.absolutePath)
        params.add("-out")
        params.add(resguardDir.absolutePath)
        if (!disable) {
            params.add("-signature")
            params.add(signingConfig.storeFile.absolutePath)
            params.add(signingConfig.storePassword)
            params.add(signingConfig.keyPassword)
            params.add(signingConfig.keyAlias)
            params.add("-signatureType")
            params.add("v2")
        }
        params.add("-finalApkPath")
        params.add(resguardApkFile.absolutePath)

        String[] runParams = toArray(params)
        // resguard enter point
        CliMain.main(runParams)
        return resguardApkFile.absolutePath
    }

    private static String[] toArray(List<String> params) {
        String[] runParams = new String[params.size()]
        for (int i = 0; i < runParams.length; i++) {
            runParams[i] = params.get(i)
        }
        return runParams
    }

    private void signApk(String inApk, String outApk, int minSdkVersion, SigningConfig signingConfig) {
        List<String> params = new ArrayList<>()
        params.add("sign")
        params.add("-v")
        params.add("--ks")
        params.add(signingConfig.storeFile.absolutePath)
        params.add("--ks-pass")
        params.add("pass:${signingConfig.storePassword}")
        params.add("--min-sdk-version")
        params.add(String.valueOf(minSdkVersion))
        params.add("--ks-key-alias")
        params.add(signingConfig.keyAlias)
        params.add("--key-pass")
        params.add("pass:${signingConfig.keyPassword}")
        params.add("--v1-signing-enabled")
        params.add("true")
        params.add("--v2-signing-enabled")
        params.add("true")
        params.add("--out")
        params.add(outApk)
        params.add(inApk)

        project.logger.lifecycle("start sign apk")
        project.logger.lifecycle("sign apk cmd: ${params.join(" ")}")
        String[] runParams = toArray(params)
        ApkSignerTool.main(runParams)
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
