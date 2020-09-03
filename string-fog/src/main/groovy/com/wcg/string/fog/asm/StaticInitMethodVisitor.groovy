package com.wcg.string.fog.asm

import com.wcg.string.fog.FogPrinter
import com.wcg.string.fog.PasswordGenerator
import com.wcg.string.fog.StringField
import org.gradle.api.logging.Logger
import org.objectweb.asm.MethodVisitor

/**
 * On 2020-09-03
 */
class StaticInitMethodVisitor extends BaseMethodVisitor {

    private String mLastStashCst
    private List<StringField> mStaticFields
    private List<StringField> mStaticFinalFields

    StaticInitMethodVisitor(MethodVisitor mv, String className, PasswordGenerator password, Logger logger,
                            FogPrinter printer, List<StringField> staticFields, List<StringField> staticFinalFields) {
        super(mv, className, password, logger, printer)
        mStaticFields = staticFields
        mStaticFinalFields = staticFinalFields
    }

    @Override
    void visitCode() {
        super.visitCode()
        // Here init static final fields.
        mStaticFinalFields.each {
            def enc = encrypt(it.value)
            if (enc.success) {
                mv.visitLdcInsn(enc.enc)
                mv.visitLdcInsn(enc.psw)
                callDecrypt(mv)
                mv.visitFieldInsn(PUTSTATIC, mClassName, it.name, STRING_DESC)
            }
        }
    }

    @Override
    void visitLdcInsn(Object cst) {
        def enc = encrypt(cst)
        if (enc.success) {
            mLastStashCst = (String) cst
            mv.visitLdcInsn(enc.enc)
            mv.visitLdcInsn(enc.psw)
            callDecrypt(mv)
        } else {
            mLastStashCst = null
            mv.visitLdcInsn(cst)
        }
    }

    @Override
    void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (mClassName == owner && mLastStashCst != null) {
            boolean contain = false
            for (StringField field : mStaticFields) {
                if (field.name == name) {
                    contain = true
                    break
                }
            }
            if (!contain) {
                mStaticFinalFields.each {
                    if (it.name == name && it.value == null) {
                        it.value = mLastStashCst
                    }
                }
            }
        }
        mLastStashCst = null
        mv.visitFieldInsn(opcode, owner, name, desc)
    }
}
