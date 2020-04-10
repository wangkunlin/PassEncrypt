package com.spoon.pass.passencode;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WcgAppResources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public Resources getResources() {
        if (mResources == null) {
            mResources = new WcgAppResources(super.getResources());
        }
        return mResources;
    }
}
