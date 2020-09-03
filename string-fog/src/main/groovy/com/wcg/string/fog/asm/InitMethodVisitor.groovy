package com.wcg.string.fog.asm

import com.wcg.string.fog.FogPrinter
import com.wcg.string.fog.PasswordGenerator
import org.gradle.api.logging.Logger
import org.objectweb.asm.MethodVisitor

/**
 * On 2020-09-03
 */
class InitMethodVisitor extends BaseMethodVisitor {

    InitMethodVisitor(MethodVisitor mv, String className, PasswordGenerator password, Logger logger,
                      FogPrinter printer) {
        super(mv, className, password, logger, printer)
    }

    @Override
    void visitLdcInsn(Object cst) {
        def enc = encrypt(cst)
        if (enc.success) {
            mv.visitLdcInsn(enc.enc)
            mv.visitLdcInsn(enc.psw)
            callDecrypt(mv)
        } else {
            mv.visitLdcInsn(cst)
        }
    }
}
