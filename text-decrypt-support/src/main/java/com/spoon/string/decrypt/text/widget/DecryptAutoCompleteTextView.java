package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;


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
