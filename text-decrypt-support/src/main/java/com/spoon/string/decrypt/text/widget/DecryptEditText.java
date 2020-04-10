package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


/**
 * On 2020-04-09
 */
public class DecryptEditText extends AppCompatEditText {

    public DecryptEditText(Context context) {
        super(context);
    }

    public DecryptEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
    }

    public DecryptEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
    }
}
