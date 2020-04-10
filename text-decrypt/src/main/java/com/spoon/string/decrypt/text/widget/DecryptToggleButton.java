package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatToggleButton;

/**
 * On 2020-04-10
 */
public class DecryptToggleButton extends AppCompatToggleButton {

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
