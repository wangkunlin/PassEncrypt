package com.wcg.manifest.editor.mode

/**
 * On 2020-06-12
 */
class Removeable {

    String what
    String key
    String value

    Removeable(String what, String key, String value) {
        this.what = what
        this.key = key
        this.value = value
    }

    static Removeable create(String what, String key, String value) {
        return new Removeable(what, key, value)
    }

    @Override
    String toString() {
        return "Removeable{" +
                "what='" + what + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}'
    }
}
