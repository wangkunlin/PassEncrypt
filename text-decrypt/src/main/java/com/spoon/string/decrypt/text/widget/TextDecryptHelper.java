package com.spoon.string.decrypt.text.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.AutoCompleteTextView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.widget.SwitchCompat;

import com.spoon.string.decrypt.text.R;

/**
 * On 2020-04-09
 */
class TextDecryptHelper {

    static void textView(TextView textView, AttributeSet attrs) {
        Context context = textView.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextDecryptHelper);

        TypedValue tv = new TypedValue();

        if (a.hasValue(R.styleable.TextDecryptHelper_android_text)) {
            a.getValue(R.styleable.TextDecryptHelper_android_text, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                textView.setText(text);
            }
        }

        if (a.hasValue(R.styleable.TextDecryptHelper_android_hint)) {
            a.getValue(R.styleable.TextDecryptHelper_android_hint, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                textView.setHint(text);
            }
        }

        a.recycle();
    }

    static void autoCompleteTextView(AutoCompleteTextView textView, AttributeSet attrs) {
        Context context = textView.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextDecryptHelper);

        TypedValue tv = new TypedValue();

        if (a.hasValue(R.styleable.TextDecryptHelper_android_completionHint)) {
            a.getValue(R.styleable.TextDecryptHelper_android_completionHint, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                textView.setCompletionHint(text);
            }
        }

        a.recycle();
    }

    static void toggleButton(ToggleButton button, AttributeSet attrs) {
        Context context = button.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextDecryptHelper);

        TypedValue tv = new TypedValue();

        if (a.hasValue(R.styleable.TextDecryptHelper_android_textOn)) {
            a.getValue(R.styleable.TextDecryptHelper_android_textOn, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                button.setTextOn(text);
            }
        }

        if (a.hasValue(R.styleable.TextDecryptHelper_android_textOff)) {
            a.getValue(R.styleable.TextDecryptHelper_android_textOff, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                button.setTextOff(text);
            }
        }

        a.recycle();
    }

    static void switchCompat(SwitchCompat sc, AttributeSet attrs) {
        Context context = sc.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextDecryptHelper);

        TypedValue tv = new TypedValue();

        if (a.hasValue(R.styleable.TextDecryptHelper_android_textOn)) {
            a.getValue(R.styleable.TextDecryptHelper_android_textOn, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                sc.setTextOn(text);
            }
        }

        if (a.hasValue(R.styleable.TextDecryptHelper_android_textOff)) {
            a.getValue(R.styleable.TextDecryptHelper_android_textOff, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                sc.setTextOff(text);
            }
        }

        a.recycle();
    }

    static void switchOrigin(Switch sc, AttributeSet attrs) {
        Context context = sc.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextDecryptHelper);

        TypedValue tv = new TypedValue();

        if (a.hasValue(R.styleable.TextDecryptHelper_android_textOn)) {
            a.getValue(R.styleable.TextDecryptHelper_android_textOn, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                sc.setTextOn(text);
            }
        }

        if (a.hasValue(R.styleable.TextDecryptHelper_android_textOff)) {
            a.getValue(R.styleable.TextDecryptHelper_android_textOff, tv);
            if (tv.type == TypedValue.TYPE_STRING && tv.resourceId != 0) {
                CharSequence text = context.getText(tv.resourceId);
                sc.setTextOff(text);
            }
        }

        a.recycle();
    }
}
