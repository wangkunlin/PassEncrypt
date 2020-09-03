package com.wcg.string.fog.asm

import com.wcg.string.fog.FogPrinter
import com.wcg.string.fog.PasswordGenerator
import com.wcg.string.fog.StringField
import com.wcg.string.fog.Utils
import org.gradle.api.logging.Logger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * On 2020-09-03
 */
class FogClassVisitor extends ClassVisitor implements Opcodes, IConstants {

    private String mClassName

    private List<StringField> mStaticFinalFields = new ArrayList<>()
    private List<StringField> mStaticFields = new ArrayList<>()
    private List<StringField> mFinalFields = new ArrayList<>()

    private boolean mStaticInitExists = false
    private PasswordGenerator mPasswordGenerator
    private Logger mLogger
    private FogPrinter mPrinter

    FogClassVisitor(String password, ClassVisitor cv, Logger logger, FogPrinter printer) {
        super(Opcodes.ASM5, cv)
        mPasswordGenerator = new PasswordGenerator(password)
        mLogger = logger
        mPrinter = printer
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName,
               String[] interfaces) {
        mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    private static boolean isStatic(int access) {
        return (access & ACC_STATIC) != 0
    }

    private static boolean isFinal(int access) {
        return (access & ACC_FINAL) != 0
    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (desc == STRING_DESC && name != null) {
            // static final, in this condition, the value is null or not null.
            if (isStatic(access) && isFinal(access)) {
                mStaticFinalFields.add(StringField.create(name, value))
                mLogger.lifecycle("static final field: ${name} = ${value}")
            }

            // static, in this condition, the value is null.
            if (isStatic(access) && !isFinal(access)) {
                mStaticFields.add(StringField.create(name, value))
                mLogger.lifecycle("static field: ${name} = ${value}")
            }

            // final, in this condition, the value is null or not null.
            if (!isStatic(access) && isFinal(access)) {
                mFinalFields.add(StringField.create(name, value))
                mLogger.lifecycle("final field: ${name} = ${value}")
            }

            // normal, in this condition, the value is null.
            if (!isStatic(access) && !isFinal(access)) {
                mLogger.lifecycle("field: ${name} = ${value}")
            }
            value = null
        }
        return super.visitField(access, name, desc, signature, value)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions)
        if (STATIC_INIT == name) {
            mStaticInitExists = true
            mv = new StaticInitMethodVisitor(mv, mClassName, mPasswordGenerator, mLogger, mPrinter,
                    mStaticFields, mStaticFinalFields)
        } else if (INIT == name) {
            mv = new InitMethodVisitor(mv, mClassName, mPasswordGenerator, mLogger, mPrinter)
        } else {
            mv = new NormalMethodVisitor(mv, mClassName, mPasswordGenerator, mLogger, mPrinter,
                    mStaticFinalFields, mFinalFields)
        }
        return mv
    }

    @Override
    void visitEnd() {
        if (!mStaticInitExists && !mStaticFinalFields.empty) {
            MethodVisitor mv = cv.visitMethod(ACC_STATIC, STATIC_INIT, EMPTY_VOID_METHOD_DESC,
                    null, null)
            mv.visitCode()
            mStaticFinalFields.each {
                def enc = Utils.enc(it.value, mPasswordGenerator.gen())
                mLogger.lifecycle("encrypt ${it.value} result: success=${enc.success}, value=${enc.enc}, password=${enc.psw}")
                if (enc.success) {
                    mPrinter.print(mClassName, it.value, enc.enc, enc.psw)
                    mv.visitLdcInsn(enc.enc)
                    mv.visitLdcInsn(enc.psw)
                    mv.visitMethodInsn(INVOKESTATIC, FOG_CLASS_NAME, FOG_DEC_METHOD,
                            FOG_DEC_METHOD_DESC, false)
                    mv.visitFieldInsn(PUTSTATIC, mClassName, it.name, STRING_DESC)
                }
            }
            mv.visitInsn(RETURN)
            mv.visitMaxs(1, 0)
            mv.visitEnd()
        }
        cv.visitEnd()
    }
}
