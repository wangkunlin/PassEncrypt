package com.wcg.proguard.dictionary

/**
 * On 2020-06-17
 */
class RandomChars {

    private static final char[] CHARS = [
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '_'
    ]

    private char[] mDic

    private List<String> mLines = new ArrayList<>()

    RandomChars(String dic) {
        if (dic == null || dic.length() < 5) {
            mDic = CHARS
        } else {
            mDic = dic.chars
        }
    }

    String generate() {
        int len = 2 + Math.random() * 8
        StringBuilder sb = new StringBuilder()
        for (int i = 0; i < len; i++) {
            sb.append(randomChar(i == 0))
        }
        String line = sb.toString()

        if (mLines.contains(line)) {
            return generate()
        }
        mLines.add(line)

        return line
    }

    private char randomChar(boolean first) {
        int index = Math.random() * mDic.length
        char c = mDic[index]
        if (c == " ".charAt(0)) {
            return randomChar(first)
        }
        if (first && (c >= '0' && c <= '9')) {
            return randomChar(true)
        }
        return c
    }
}
