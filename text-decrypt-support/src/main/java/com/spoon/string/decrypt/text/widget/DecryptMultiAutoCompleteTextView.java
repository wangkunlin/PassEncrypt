package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.AttributeSet;


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
