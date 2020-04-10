package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

/**
 * On 2020-04-09
 */
public class DecryptMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {

    public DecryptMultiAutoCompleteTextView(Context context) {
        super(context);
    }

    public DecryptMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.autoCompleteTextView(this, attrs);
    }

    public DecryptMultiAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextDecryptHelper.textView(this, attrs);
        TextDecryptHelper.autoCompleteTextView(this, attrs);
    }
}
