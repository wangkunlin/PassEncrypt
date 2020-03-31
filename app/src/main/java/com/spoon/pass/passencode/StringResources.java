package com.spoon.pass.passencode;

import android.content.res.Resources;

import androidx.annotation.NonNull;

/**
 * Created by wangkunlin
 * On 2020-03-31
 */
public class StringResources extends Resources {

    private Resources mRes;

    public StringResources(Resources res) {
        super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration());
        mRes = res;
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        return WcgAppString.getString(mRes, id);
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return WcgAppString.getString(mRes, id, formatArgs);
    }

    @NonNull
    @Override
    public CharSequence getText(int id) throws NotFoundException {
        return WcgAppString.getText(mRes, id);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        return WcgAppString.getText(mRes, id, def);
    }

    @NonNull
    @Override
    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        return WcgAppString.getQuantityText(mRes, id, quantity);
    }

    @NonNull
    @Override
    public String getQuantityString(int id, int quantity) throws NotFoundException {
        return WcgAppString.getQuantityString(mRes, id, quantity);
    }

    @NonNull
    @Override
    public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException {
        return WcgAppString.getQuantityString(mRes, id, quantity, formatArgs);
    }
}
