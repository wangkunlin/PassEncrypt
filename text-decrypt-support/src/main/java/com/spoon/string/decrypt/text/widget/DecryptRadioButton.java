package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;


/**
 * On 2020-04-09
 */
public class DecryptRadioButton extends AppCompatRadioButton {

    public DecryptRadioButton(Context context) {
        super(context);
    }

    public DecryptRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
    }

    public DecryptRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
    }
}
