package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;

/**
 * On 2020-04-10
 */
public class DecryptSwitch extends Switch {

    public DecryptSwitch(Context context) {
        super(context);
    }

    public DecryptSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.switchOrigin(this, attrs);
    }

    public DecryptSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.switchOrigin(this, attrs);
    }
}
