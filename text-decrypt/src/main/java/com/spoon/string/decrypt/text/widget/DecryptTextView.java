package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * On 2020-04-09
 */
public class DecryptTextView extends AppCompatTextView {

    public DecryptTextView(Context context) {
        super(context);
    }

    public DecryptTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
    }

    public DecryptTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
    }
}
