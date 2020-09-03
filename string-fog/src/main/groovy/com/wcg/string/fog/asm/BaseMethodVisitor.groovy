package com.wcg.string.fog.asm

import com.wcg.string.fog.FogPrinter
import com.wcg.string.fog.PasswordGenerator
import com.wcg.string.fog.StringEnc
import com.wcg.string.fog.Utils
import org.gradle.api.logging.Logger
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * On 2020-09-03
 */
abstract class BaseMethodVisitor extends MethodVisitor implements Opcodes, IConstants {

    protected PasswordGenerator mPasswordGenerator
    protected String mClassName
    protected Logger mLogger
    private FogPrinter mPrinter

    BaseMethodVisitor(MethodVisitor mv, String className, PasswordGenerator password, Logger logger, FogPrinter printer) {
        super(Opcodes.ASM5, mv)
        mClassName = className
        mPasswordGenerator = password
        mLogger = logger
        mPrinter = printer
    }

    protected StringEnc encrypt(Object src) {
        StringEnc enc = Utils.enc(src, mPasswordGenerator.gen())
        if (enc.success) {
            mLogger.lifecycle("encrypt ${src} result: success=${enc.success}, value=${enc.enc}, password=${enc.psw}")
            mPrinter.print(mClassName, src, enc.enc, enc.psw)
        }
        return enc
    }

    protected static void callDecrypt(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, FOG_CLASS_NAME, FOG_DEC_METHOD,
                FOG_DEC_METHOD_DESC, false)
    }
}
