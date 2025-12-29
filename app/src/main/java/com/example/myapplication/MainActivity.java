package com.example.myapplication;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.fragment.AddFrag;
import com.example.myapplication.fragment.HomeFrag;
import com.example.myapplication.fragment.SettingFrag;
import com.example.myapplication.fragment.ViewFrag;
import com.example.myapplication.util.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends BaseActivity {

    public static final String CHANNEL_ID = "ExpenseChannel";
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(v);
            if (controller != null) {
                controller.setAppearanceLightStatusBars(true);
            }
            return insets;
        });

        createNotificationChannel();
        askForNotificationPermission();


        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. You can now post notifications.
                Log.d("PERMISSION", "Notification permission granted.");
            } else {
                Toast.makeText(getContext(), "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expense Notifications";
            String description = "Channel for expense tracking notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void askForNotificationPermission() {
        // This is only necessary for API level 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
