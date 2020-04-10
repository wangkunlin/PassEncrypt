package com.spoon.string.decrypt.text.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * On 2020-04-10
 */
@SuppressLint("AppCompatCustomView")
public class DecryptToggleButton extends ToggleButton {

    public DecryptToggleButton(Context context) {
        super(context);
    }

    public DecryptToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.toggleButton(this, attrs);
    }

    public DecryptToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.toggleButton(this, attrs);
    }
}
