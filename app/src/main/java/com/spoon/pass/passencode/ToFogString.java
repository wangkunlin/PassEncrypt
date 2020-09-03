package com.spoon.pass.passencode;

import org.xxtea.XXTEA;

/**
 * On 2020-09-04
 */

@TestAnno("test_fog")
public class ToFogString {
    private String fog1 = "fog1v";
    private static String fog2 = "fog2v";
    private final String fog3 = "fog3v";
    private static final String fog4 = "fog4v";
    private static final String fog5;

    static {
        fog5 = "fog5v";
    }

    private static void fog() {
        fog2 = "fog()v";
    }

    private void fog2(String a, String b) {
        XXTEA.decryptBase64StringToString(a, b);
        System.out.println("fog2()v");
    }
}
