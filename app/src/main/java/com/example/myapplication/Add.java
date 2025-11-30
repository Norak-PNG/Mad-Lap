package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Import Toast

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public  void  onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button add = view.findViewById(R.id.button);
        EditText amount = view.findViewById(R.id.editTextText1);
        EditText currency = view.findViewById(R.id.editTextText2);
        EditText category = view.findViewById(R.id.editTextText3);
        EditText remark = view.findViewById(R.id.editTextText4);

        add.setOnClickListener(v -> {
            String amount_send = amount.getText().toString();
            String currency_send = currency.getText().toString();
            String category_send = category.getText().toString();
            String remark_send = remark.getText().toString();
            String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

            // 4. Call your new generator functions
            String generatedId = generateUniqueId();
            String currentDateString = generateIso8601Date();

            // You can now use these generated values
            Log.d("GENERATOR", "Generated ID: " + generatedId);
            Log.d("GENERATOR", "Generated Date: " + currentDateString);

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
    private void sendPostToServer(Post post) {
        // Get API service instance
        JsonPlaceholderApi apiService = RetrofitClient.getRetrofitInstance().create(JsonPlaceholderApi.class);

        // Create the call using the new createPost method
        Call<Post> call = apiService.postData(post);

        // Enqueue the call to run asynchronously
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                if (!isAdded()) return; // Check if fragment is still active

                if (response.isSuccessful()) {
                    // Server responded with success (e.g., status 201 Created)
                    Toast.makeText(getContext(), "Expense Added Successfully!", Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                    Log.d("ADD_FRAGMENT", "Post successful: " + response.body().toString());
                    // Optionally, you can clear the EditText fields here
                } else {
                    // Server responded with an error code (4xx or 5xx)
                    Toast.makeText(getContext(), "Failed to add expense. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("ADD_FRAGMENT", "API Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                if (!isAdded()) return; // Check if fragment is still active
                // Network error (e.g., no internet, server down)
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ADD_FRAGMENT", "Network Failure: " + t.getMessage());
            }
        });
    }

    // Function to generate a unique ID (UUID)
    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    // Function to generate a date string in ISO 8601 format (UTC)
    private String generateIso8601Date() {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format.format(new Date());
    }
}
