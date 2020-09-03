package com.wcg.string.fog

/**
 * On 2020-09-03
 */
class StringField {
    String name
    String value

    StringField(String name, String value) {
        this.name = name
        this.value = value
    }

    static StringField create(String name, Object value) {
        return new StringField(name, (String) value)
    }
}
