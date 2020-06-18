package com.wcg.proguard.dictionary

/**
 * On 2020-06-17
 */
class DictionaryExtension {

    static final int MIN_COUNT = 8000

    private String fromChars
    private Integer count = MIN_COUNT

    Integer getCount() {
        return count
    }

    void setCount(Integer count) {
        this.count = count
    }

    String getFromChars() {
        return fromChars
    }

    void setFromChars(String fromChars) {
        this.fromChars = fromChars
    }

    int constraintedCount() {
        if (count == null) {
            return MIN_COUNT
        }
        if (count < MIN_COUNT) {
            return MIN_COUNT
        }
        return count
    }

}
