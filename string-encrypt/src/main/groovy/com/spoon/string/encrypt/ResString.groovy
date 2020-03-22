package com.spoon.string.encrypt

class ResString {

    String name

    String value

    boolean keep = false

    static ResString password(String resId, String psw) {
        ResString resString = new ResString()
        resString.value = psw
        resString.name = resId
        resString.keep = true
        return resString
    }

    static ResString string(String name, String value, boolean keep) {
        ResString resString = new ResString()
        resString.value = value
        resString.name = name
        resString.keep = keep
        return resString
    }
}
