package com.wcg.string.fog.asm

import org.xxtea.XXTEA

/**
 * On 2020-09-03
 */
interface IConstants {

    String STATIC_INIT = "<clinit>"

    String INIT = "<init>"

    String STRING_DESC = "Ljava/lang/String;"

    String FOG_CLASS_NAME = XXTEA.class.name.replace('.', '/')

    String FOG_DEC_METHOD = "decryptBase64StringToString"
    String FOG_DEC_METHOD_DESC = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"

    String EMPTY_VOID_METHOD_DESC = "()V"
}
