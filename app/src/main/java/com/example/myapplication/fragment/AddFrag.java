package com.example.myapplication.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.CameraActivity;
import com.example.myapplication.NewCategoryActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Category;
import com.example.myapplication.database.JsonPlaceholderApi;
import com.example.myapplication.database.Post;
import com.example.myapplication.database.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFrag extends Fragment {

    private static final String HIGH_EXPENSE_CHANNEL_ID = "high_expense_channel";
    private AppDatabase appDatabase;
    private ArrayList<String> categoryList;
    private ArrayAdapter<String> categoryAdapter;
    private Spinner categorySpinner;
    private Spinner currency;
    private ActivityResultLauncher<Intent> newCategoryLauncher;
    private ImageView capturedImageView;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    Button add_category;
    Button openCameraButton;
    Button galleryButton;
    Button add;
    EditText amount;
    EditText remark;
    private String  imageUri;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newCategoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newCategory = result.getData().getStringExtra("newCategory");
                        if (newCategory != null && !newCategory.isEmpty()) {
                            if (!categoryList.contains(newCategory)) {
                                categoryList.add(newCategory);
                                categoryAdapter.notifyDataSetChanged();
                                categorySpinner.setSelection(categoryAdapter.getPosition(newCategory));
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_view(view);
        appDatabase = AppDatabase.getDatabase(requireContext().getApplicationContext());
        init_adapter();
        loadCategories();
        init_listener();
        createNotificationChannel();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }

    private void init_adapter () {
        categoryList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayList<String> currencyList = new ArrayList<>();
        currencyList.add("USD");
        currencyList.add("KHR");
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, currencyList);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(currencyAdapter);
    }

    private void init_listener () {
        add_category.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewCategoryActivity.class);
            newCategoryLauncher.launch(intent);
        });
        openCameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CameraActivity.class);
            cameraLauncher.launch(intent);
        });
        add.setOnClickListener(v -> {
            String amount_send = amount.getText().toString();
            String currency_send = currency.getSelectedItem().toString();
            String category_send = categorySpinner.getSelectedItem().toString();
            Log.d("ADD_FRAGMENT", category_send);
            String remark_send = remark.getText().toString();
            String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

            String generatedId = generateUniqueId();
            String currentDateString = generateIso8601Date();



            if (amount_send.isEmpty() || currency_send.isEmpty() || category_send.isEmpty()) {
                Toast.makeText(getContext(), "Amount, Currency, and Category cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            int amountValue = Integer.parseInt(amount_send);
            if (("USD".equals(currency_send) && amountValue >= 100) || ("KHR".equals(currency_send) && amountValue >= 400000)) {
                showHighExpenseNotification(amountValue, currency_send);
            }

            Post data = new Post();
            data.id = generatedId;
            data.amount = amountValue;
            data.currency = currency_send;
            data.category = category_send;
            data.remark = remark_send;
            data.createdBy = email;
            data.createdDate = currentDateString;
            data.uri = imageUri;


            sendPostToServer(data);

        });
        //
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Glide.with(this).load(data.getData()).into(capturedImageView);
                            imageUri = data.getData().toString();
                        }
                    }
                });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Glide.with(this).load(uri).into(capturedImageView);
                        imageUri = uri.toString();
                    }
                });
        //
        galleryButton.setOnClickListener(v -> galleryLauncher.launch("image/*"));
    }
    private  void init_view (View view) {
        capturedImageView = view.findViewById(R.id.captured_image_view);
        openCameraButton = view.findViewById(R.id.open_camera_button);
        galleryButton = view.findViewById(R.id.galleryButton);
        add = view.findViewById(R.id.button);
        amount = view.findViewById(R.id.editTextText1);
        remark = view.findViewById(R.id.editTextText4);
        categorySpinner = view.findViewById(R.id.spinner2);
        currency = view.findViewById(R.id.currency);
        add_category = view.findViewById(R.id.button2);
        capturedImageView.setImageResource(R.drawable.default_image);


        String view_email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        TextView email_view = view.findViewById(R.id.name);
        email_view.setText(view_email);
    }
    private void loadCategories() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Category> dbCategories = appDatabase.categoryDao().getAllCategories();
            final List<String> categoryNames = new ArrayList<>();
            for (Category cat : dbCategories) {
                categoryNames.add(cat.name);
            }

            if(isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    categoryList.clear();

                    String[] staticCategories = getResources().getStringArray(R.array.categories);
                    categoryList.addAll(Arrays.asList(staticCategories));

                    for (String dbCategoryName : categoryNames) {
                        if (!categoryList.contains(dbCategoryName)) {
                            categoryList.add(dbCategoryName);
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void sendPostToServer(Post post) {
        JsonPlaceholderApi apiService = RetrofitClient.getRetrofitInstance().create(JsonPlaceholderApi.class);

        Call<Post> call = apiService.postData(post);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Expense Added Successfully!", Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                    Log.d("ADD_FRAGMENT", "Post successful: " + response.body());
                } else {
                    Toast.makeText(getContext(), "Failed to add expense. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("ADD_FRAGMENT", "API Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ADD_FRAGMENT", "Network Failure: " + t.getMessage());
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "High Expense Warning";
            String description = "Channel for high expense warnings";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(HIGH_EXPENSE_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showHighExpenseNotification(int amount, String currency) {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Please grant notification permission to receive warnings.", Toast.LENGTH_SHORT).show();
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), HIGH_EXPENSE_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("High Expense Warning")
                .setContentText("You have added an expense of " + amount + " " + currency)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(1, builder.build());
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    private String generateIso8601Date() {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format.format(new Date());
    }
}
