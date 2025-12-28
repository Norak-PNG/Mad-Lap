package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.database.JsonPlaceholderApi;
import com.example.myapplication.Login;
import com.example.myapplication.database.Post;
import com.example.myapplication.R;
import com.example.myapplication.database.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFrag extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        TextView email_view = view.findViewById(R.id.name);
        email_view.setText(email);

        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "You have been signed out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        });

        fetchExpenses();
    }

    private void fetchExpenses() {
        JsonPlaceholderApi apiService = RetrofitClient.getRetrofitInstance().create(JsonPlaceholderApi.class);

        Call<List<Post>> call = apiService.getData();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (!isAdded()) {
                    return;
                }

                if (!response.isSuccessful()) {
                    Log.e("Retrofit_Fragment", "API call failed with code: " + response.code());
                    return;
                }

                List<Post> expenses = response.body();
                if (expenses != null && !expenses.isEmpty()) {
                    String currency = expenses.get(expenses.size() - 1).getCurrency();
                    int amount = expenses.get(expenses.size() - 1).getAmount();

                    TextView user = getView().findViewById(R.id.user);
                    TextView LatestExpense = getView().findViewById(R.id.LatestExpense);
                    TextView Date = getView().findViewById(R.id.Date);
                    ImageView imageView = getView().findViewById(R.id.imageView);

                    LatestExpense.setText(amount + " " + currency);
                    Date.setText(expenses.get(expenses.size() - 1).getCreatedDate());
                    user.setText(expenses.get(expenses.size() - 1).getCreatedBy());


                    if (expenses.get(expenses.size() - 1).getUri() != null) {
                        imageView.setImageURI(expenses.get(expenses.size() - 1).getUri());
                    } else {
                        imageView.setImageResource(R.drawable.default_image);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    return;
                }
                Log.e("Retrofit_Fragment", "API call failed: " + t.getMessage());
            }
        });
    }
}
