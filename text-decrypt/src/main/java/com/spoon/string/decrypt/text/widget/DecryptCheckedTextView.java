package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckedTextView;

/**
 * On 2020-04-09
 */
public class DecryptCheckedTextView extends AppCompatCheckedTextView {

    public DecryptCheckedTextView(Context context) {
        super(context);
    }

    public DecryptCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
    }

    public DecryptCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
    }
}
