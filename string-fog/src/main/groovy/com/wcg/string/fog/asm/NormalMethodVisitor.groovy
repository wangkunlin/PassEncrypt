package com.wcg.string.fog.asm

import com.wcg.string.fog.FogPrinter
import com.wcg.string.fog.PasswordGenerator
import com.wcg.string.fog.StringField
import org.gradle.api.logging.Logger
import org.objectweb.asm.MethodVisitor

/**
 * On 2020-09-03
 */
class NormalMethodVisitor extends BaseMethodVisitor {

    private List<StringField> mStaticFinalFields
    private List<StringField> mFinalFields

    NormalMethodVisitor(MethodVisitor mv, String className, PasswordGenerator password, Logger logger,
                        FogPrinter printer, List<StringField> staticFinalFields, List<StringField> finalFields) {
        super(mv, className, password, logger, printer)
        mStaticFinalFields = staticFinalFields
        mFinalFields = finalFields
    }

    @Override
    void visitLdcInsn(Object cst) {
        def enc = encrypt(cst)
        if (enc.success) {
            for (StringField field : mStaticFinalFields) {
                if (cst == field.value) {
                    mv.visitFieldInsn(GETSTATIC, mClassName, field.name, STRING_DESC)
                    return
                }
            }

            for (StringField field : mFinalFields) {
                if (cst == field.value) {
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitFieldInsn(GETFIELD, mClassName, field.name, STRING_DESC)
                    return
                }
            }
            mv.visitLdcInsn(enc.enc)
            mv.visitLdcInsn(enc.psw)
            callDecrypt(mv)
        } else {
            mv.visitLdcInsn(cst)
        }
    }
}
