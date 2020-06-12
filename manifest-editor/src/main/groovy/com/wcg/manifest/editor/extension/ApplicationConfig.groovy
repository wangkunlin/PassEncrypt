package com.wcg.manifest.editor.extension

import com.wcg.manifest.editor.mode.Removeable

/**
 * On 2020-06-12
 */
class ApplicationConfig {

    private Set<Removeable> mToRemove = new HashSet<>()

    void remove(String node, String attrName, String attrValue) {
        mToRemove.add(Removeable.create(node, attrName, attrValue))
    }

    Set<Removeable> getToRemove() {
        return mToRemove
    }

    @Override
    String toString() {
        return mToRemove.join(",")
    }
}
