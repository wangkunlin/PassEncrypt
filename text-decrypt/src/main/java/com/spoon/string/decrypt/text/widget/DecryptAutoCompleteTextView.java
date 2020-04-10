package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

/**
 * On 2020-04-09
 */
public class DecryptAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public DecryptAutoCompleteTextView(Context context) {
        super(context);
    }

    public DecryptAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.autoCompleteTextView(this, attrs);
    }

    public DecryptAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.autoCompleteTextView(this, attrs);
    }
}
