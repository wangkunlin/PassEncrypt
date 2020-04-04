package com.spoon.rename.pkg

class RenameExtension {

    private String mApplicationId

    void applicationId(String pkg) {
        mApplicationId = pkg
    }

    String getApplicationId() {
        return mApplicationId
    }
}