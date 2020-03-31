package com.spoon.pass.passencode;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WcgAppResources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.txt);
        tv.setText(WcgAppString.getString(getResources(), R.string.to_enc));
    }

    @Override
    public Resources getResources() {
        if (mResources == null) {
            mResources = new WcgAppResources(super.getResources());
        }
        return mResources;
    }
}
