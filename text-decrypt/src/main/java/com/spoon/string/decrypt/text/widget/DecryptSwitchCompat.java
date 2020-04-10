package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.SwitchCompat;

/**
 * On 2020-04-10
 */
public class DecryptSwitchCompat extends SwitchCompat {

    public DecryptSwitchCompat(Context context) {
        super(context);
    }

    public DecryptSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.switchCompat(this, attrs);
    }

    public DecryptSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.switchCompat(this, attrs);
    }
}
