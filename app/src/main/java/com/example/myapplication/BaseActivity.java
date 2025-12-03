package com.example.myapplication;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.util.LocateHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocateHelper.setLocale(newBase));
    }
}
