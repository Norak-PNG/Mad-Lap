package com.example.myapplication.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.CameraActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.database.JsonPlaceholderApi;
import com.example.myapplication.NewCategoryActivity;
import com.example.myapplication.database.Post;
import com.example.myapplication.R;
import com.example.myapplication.database.RetrofitClient;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Category;
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

    private AppDatabase appDatabase;
    private ArrayList<String> categoryList;
    private ArrayAdapter<String> categoryAdapter;
    private Spinner categorySpinner;
    private ActivityResultLauncher<Intent> newCategoryLauncher;
    private ImageView capturedImageView;
    private Button galleryButton;
    private Button openCameraButton;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        capturedImageView = view.findViewById(R.id.captured_image_view);
        openCameraButton = view.findViewById(R.id.open_camera_button);
        galleryButton = view.findViewById(R.id.galleryButton);
        Button add = view.findViewById(R.id.button);
        EditText amount = view.findViewById(R.id.editTextText1);
        EditText currency = view.findViewById(R.id.editTextText2);
        EditText remark = view.findViewById(R.id.editTextText4);
        categorySpinner = view.findViewById(R.id.spinner2);
        Button add_category = view.findViewById(R.id.button2);

        appDatabase = AppDatabase.getDatabase(requireContext().getApplicationContext());

        categoryList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        loadCategories();

        add_category.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewCategoryActivity.class);
            newCategoryLauncher.launch(intent);
        });


        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri imageUri = data.getData();
                            Log.d("MainActivity", "Image URI: " + imageUri.toString());
                            capturedImageView.setImageURI(imageUri);
                        }
                    }
                });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {

                        capturedImageView.setImageURI(uri);

                        String imageUriString = uri.toString();
                        Log.d("MainActivity", "Gallery Image URI as String: " + imageUriString);
                    }
                });

        openCameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CameraActivity.class);
            cameraLauncher.launch(intent);
        });

        galleryButton.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        add.setOnClickListener(v -> {
            String amount_send = amount.getText().toString();
            String currency_send = currency.getText().toString();
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

            Post data = new Post();
            data.id = generatedId;
            data.amount = Integer.parseInt(amount_send);
            data.currency = currency_send;
            data.category = category_send;
            data.remark = remark_send;
            data.createdBy = email;
            data.createdDate = currentDateString;


            sendPostToServer(data);

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
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
                    Log.d("ADD_FRAGMENT", "Post successful: " + response.body().toString());
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

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    private String generateIso8601Date() {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format.format(new Date());
    }
}
