package com.spoon.pass.passencode;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv =findViewById(R.id.txt);
        tv.setText(UrlConstantEncrypted.new_url);
    }
}
