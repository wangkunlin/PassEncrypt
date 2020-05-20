package com.spoon.pass.passencode;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;

public class MainActivity extends AppCompatActivity {

    private WcgAppResources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NestedScrollingParent3 nsp;
        NestedScrollingParentHelper nsph;
        NestedScrollingChildHelper nsch;
    }

    @Override
    public Resources getResources() {
        if (mResources == null) {
            mResources = new WcgAppResources(super.getResources());
        }
        return mResources;
    }
}
