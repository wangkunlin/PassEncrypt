package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;


/**
 * On 2020-04-09
 */
public class DecryptCheckBox extends AppCompatCheckBox {

    public DecryptCheckBox(Context context) {
        super(context);
    }

    public DecryptCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
    }

    public DecryptCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
    }
}
