package com.spoon.string.decrypt.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatViewInflater;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatToggleButton;

import com.spoon.string.decrypt.text.widget.DecryptAutoCompleteTextView;
import com.spoon.string.decrypt.text.widget.DecryptButton;
import com.spoon.string.decrypt.text.widget.DecryptCheckBox;
import com.spoon.string.decrypt.text.widget.DecryptCheckedTextView;
import com.spoon.string.decrypt.text.widget.DecryptEditText;
import com.spoon.string.decrypt.text.widget.DecryptMultiAutoCompleteTextView;
import com.spoon.string.decrypt.text.widget.DecryptRadioButton;
import com.spoon.string.decrypt.text.widget.DecryptSwitch;
import com.spoon.string.decrypt.text.widget.DecryptTextView;
import com.spoon.string.decrypt.text.widget.DecryptToggleButton;

/**
 * On 2020-04-09
 */
@Keep
public class DecryptViewInflater extends AppCompatViewInflater {

    @NonNull
    @Override
    protected AppCompatTextView createTextView(Context context, AttributeSet attrs) {
        return new DecryptTextView(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatButton createButton(Context context, AttributeSet attrs) {
        return new DecryptButton(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatEditText createEditText(Context context, AttributeSet attrs) {
        return new DecryptEditText(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatCheckBox createCheckBox(Context context, AttributeSet attrs) {
        return new DecryptCheckBox(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatRadioButton createRadioButton(Context context, AttributeSet attrs) {
        return new DecryptRadioButton(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatCheckedTextView createCheckedTextView(Context context, AttributeSet attrs) {
        return new DecryptCheckedTextView(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatAutoCompleteTextView createAutoCompleteTextView(Context context, AttributeSet attrs) {
        return new DecryptAutoCompleteTextView(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatMultiAutoCompleteTextView createMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        return new DecryptMultiAutoCompleteTextView(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatToggleButton createToggleButton(Context context, AttributeSet attrs) {
        return new DecryptToggleButton(context, attrs);
    }

    @Nullable
    @Override
    protected View createView(Context context, String name, AttributeSet attrs) {
        switch (name) {
            case "Switch":
                return new DecryptSwitch(context, attrs);
        }
        return super.createView(context, name, attrs);
    }
}
