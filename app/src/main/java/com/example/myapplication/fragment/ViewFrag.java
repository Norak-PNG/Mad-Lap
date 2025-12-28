package com.example.myapplication.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.JsonPlaceholderApi;
import com.example.myapplication.database.Post;
import com.example.myapplication.R;
import com.example.myapplication.RecyclerViewAd;
import com.example.myapplication.database.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewFrag extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAd recyclerViewAd;
    private List<Post> expenseList;

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expenseList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        TextView email_view = view.findViewById(R.id.name);
        email_view.setText(email);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAd = new RecyclerViewAd(expenseList, getContext());
        recyclerView.setAdapter(recyclerViewAd);

        fetchExpenses();
    }


    private void fetchExpenses() {
        JsonPlaceholderApi apiService = RetrofitClient.getRetrofitInstance().create(JsonPlaceholderApi.class);
        Call<List<Post>> call = apiService.getData();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (!isAdded()) return; // Check if fragment is still active

                if (response.isSuccessful() && response.body() != null) {
                    expenseList.addAll(response.body());
                    recyclerViewAd.notifyDataSetChanged();

                    Log.d("VIEW_FRAGMENT", "Data fetched and adapter updated.");
                } else {
                    Toast.makeText(getContext(), "Failed to fetch data: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("VIEW_FRAGMENT", "API call failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VIEW_FRAGMENT", "API call failure: " + t.getMessage());
            }
        });
    }
}
