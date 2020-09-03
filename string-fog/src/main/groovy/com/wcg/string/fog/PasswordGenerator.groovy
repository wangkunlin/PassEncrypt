package com.wcg.string.fog

/**
 * On 2020-09-02
 */
class PasswordGenerator {

    private static final char[] RANDOM_PASSWORD_CHARS = [
            'F', 'G', 'H', 'I', 'J', 'K', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', '4', '5', '6', '7', 'W',
            '\\', '|', '{', '}', 'a', 'b', 'c', 'A', 'B',
            'C', 'D', 'E', '8', '9', 'd', 'e', 'f', 'g',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 'L', 'M', 'N', 'O', 't', 'u', 'v', 'w',
            '1', '2', 'X', 'Y', 'Z', '`', '3', 'x', 'y',
            'z', '0', '+', '/', 'h', 'i', '!', '^', '&',
            '*', '(', ')', ':', ';', '@', '#', '$', '%',
            '\'', '"', ',', '.', '~', '[', ']', '_', '='
    ]

    private String mPassword

    PasswordGenerator(String password) {
        mPassword = password
    }

    String gen() {
        if (mPassword == null || mPassword.trim().empty || mPassword.trim().length() < 6) {
            return random()
        }
        return mPassword.trim()
    }

    private static String random() {
        return random(0)
    }

    private static String random(int length) {
        if (length < 6) {
            length = new Random().nextInt(7) + 6
        }

        StringBuilder psw = new StringBuilder()
        for (int i = 0; i < length; ++i) {
            int index = new Random().nextInt(RANDOM_PASSWORD_CHARS.length)
            psw.append(RANDOM_PASSWORD_CHARS[index])
        }
        return psw.toString()
    }
}
