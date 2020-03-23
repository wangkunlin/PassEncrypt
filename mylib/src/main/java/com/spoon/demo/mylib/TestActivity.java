package com.spoon.demo.mylib;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by wangkunlin
 * On 2020-03-23
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 生成的类 规则， Wcg + module name + StringGetter
        // 理论上是支持获取未加密的字符串
        String txt = WcgMylibStringGetter.getString(this, R.string.mylib_to_enc);
    }
}
