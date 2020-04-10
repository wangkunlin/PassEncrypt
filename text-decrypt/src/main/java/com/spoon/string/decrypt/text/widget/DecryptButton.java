package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

/**
 * On 2020-04-10
 */
public class DecryptButton extends AppCompatButton {

    public DecryptButton(Context context) {
        super(context);
    }

    public DecryptButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
    }

    public DecryptButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
    }
}
