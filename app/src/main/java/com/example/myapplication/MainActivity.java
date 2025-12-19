package com.example.myapplication;

import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.fragment.AddFrag;
import com.example.myapplication.fragment.HomeFrag;
import com.example.myapplication.fragment.SettingFrag;
import com.example.myapplication.fragment.ViewFrag;
import com.example.myapplication.util.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            replaceFragment(new HomeFrag());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeFrag());
                return true;
            } else if (itemId == R.id.add) {
                replaceFragment(new AddFrag());
                return true;
            } else if (itemId == R.id.view) {
                replaceFragment(new ViewFrag());
                return true;
            } else if (itemId == R.id.setting) {
                replaceFragment(new SettingFrag());
                return true;
        }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
