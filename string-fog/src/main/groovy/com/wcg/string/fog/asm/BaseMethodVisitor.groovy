package com.wcg.string.fog.asm

import com.wcg.string.fog.FogPrinter
import com.wcg.string.fog.utils.EncryptString
import com.wcg.string.fog.utils.FogLogger
import com.wcg.string.fog.utils.StringEnc
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * On 2020-09-03
 */
abstract class BaseMethodVisitor extends MethodVisitor implements Opcodes, IConstants {

    protected String mClassName
    protected FogLogger mLogger
    private FogPrinter mPrinter

    BaseMethodVisitor(MethodVisitor mv, String className, FogLogger logger, FogPrinter printer) {
        super(Opcodes.ASM5, mv)
        mClassName = className
        mLogger = logger
        mPrinter = printer
    }

    protected StringEnc encrypt(Object src) {
        StringEnc enc = EncryptString.instance.enc(src)
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
