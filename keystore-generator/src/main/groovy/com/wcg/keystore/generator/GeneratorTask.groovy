package com.wcg.keystore.generator

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.model.SigningConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import sun.security.x509.*

import java.security.*
import java.security.cert.Certificate
import java.security.cert.X509Certificate

/**
 * On 2020-07-25
 */
class GeneratorTask extends DefaultTask {

    @OutputFile
    File keystoreFile

    ApplicationVariant variant

    @TaskAction
    void taskAction() {
        if (keystoreFile != null && keystoreFile.exists()) {
            return
        }
        SigningConfig signingConfig = variant.signingConfig
        keystoreFile = signingConfig.storeFile

        if (keystoreFile != null && keystoreFile.exists()) {
            return
        }

        try {
            FileOutputStream fos = new FileOutputStream(keystoreFile)

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(4096)
            KeyPair keyPair = keyPairGenerator.generateKeyPair()

            List<Certificate> certs = new ArrayList<>()
            certs.add(generateCertificate("c=US", keyPair, 365 * 25, "SHA256withRSA"))

            Certificate[] chain = toArray(certs)

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setKeyEntry(signingConfig.keyAlias, keyPair.getPrivate(), signingConfig.keyPassword.toCharArray(), chain)
            keyStore.store(fos, signingConfig.storePassword.toCharArray())

            fos.flush()
            fos.close()
        } catch (Throwable e) {
            keystoreFile.delete()
            throw e
        }

    }

    private static Certificate[] toArray(List<Certificate> certs) {
        Certificate[] array = new Certificate[certs.size()]
        for (int i = 0; i < certs.size(); i++) {
            array[i] = certs.get(i)
        }
        return array

    }

    private static X509Certificate generateCertificate(String dn, KeyPair keyPair, int validity, String sigAlgName) {
        PrivateKey privateKey = keyPair.getPrivate()

        X509CertInfo info = new X509CertInfo()

        Date from = new Date()
        Date to = new Date(from.getTime() + validity * 1000L * 60L * 60L * 24L)

        CertificateValidity interval = new CertificateValidity(from, to)
        BigInteger serialNumber = new BigInteger(64, new SecureRandom())
        X500Name owner = new X500Name(dn)
        AlgorithmId sigAlgId = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid)

        info.set(X509CertInfo.VALIDITY, interval)
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber))
        info.set(X509CertInfo.SUBJECT, owner)
        info.set(X509CertInfo.ISSUER, owner)
        info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()))
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3))
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(sigAlgId))

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl certificate = new X509CertImpl(info)
        certificate.sign(privateKey, sigAlgName)

        // Update the algorith, and resign.
        sigAlgId = (AlgorithmId) certificate.get(X509CertImpl.SIG_ALG)
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, sigAlgId)
        certificate = new X509CertImpl(info)
        certificate.sign(privateKey, sigAlgName)
        return certificate
    }
}
