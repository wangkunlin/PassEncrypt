package com.wcg.string.fog.asm

import com.wcg.string.fog.FogPrinter
import com.wcg.string.fog.utils.FogLogger
import org.objectweb.asm.MethodVisitor
/**
 * On 2020-09-03
 */
class InitMethodVisitor extends BaseMethodVisitor {

    InitMethodVisitor(MethodVisitor mv, String className, FogLogger logger,
                      FogPrinter printer) {
        super(mv, className, logger, printer)
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
