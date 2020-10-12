package com.tencent.mm.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * On 2020-10-12
 */
public class RandomChars {

    private static final char[] CHARS = {
//            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
//            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
//            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '_'
    };

    private char[] mDic;

    private Set<String> mLines = new HashSet<>();

    public RandomChars(String dic) {
        if (dic == null || dic.length() < 5) {
            mDic = CHARS;
        } else {
            mDic = dic.toCharArray();
        }
    }

    public String generate() {
        int len = (int) (1 + Math.random() * 5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(randomChar(i == 0));
        }
        String line = sb.toString();

        if (mLines.contains(line)) {
            return generate();
        }
        mLines.add(line);

        return line;
    }

    public void offer(Collection<String> collection) {
        mLines.addAll(collection);
    }

    private char randomChar(boolean first) {
        int index = (int) (Math.random() * mDic.length);
        char c = mDic[index];
        if (c == " ".charAt(0)) {
            return randomChar(first);
        }
        if (first && (c >= '0' && c <= '9')) {
            return randomChar(true);
        }
        return c;
    }
}
