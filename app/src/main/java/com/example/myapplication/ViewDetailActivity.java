package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_detail);

        String remark = getIntent().getStringExtra("remark");
        String category = getIntent().getStringExtra("category");
        int amount = getIntent().getIntExtra("amount", 0);
        String currency = getIntent().getStringExtra("currency");
        String date = getIntent().getStringExtra("date");
        String createdBy = getIntent().getStringExtra("createdBy");
        String id = getIntent().getStringExtra("id");

        TextView remarkTextView = findViewById(R.id.remark);
        TextView categoryTextView = findViewById(R.id.category);
        TextView amountTextView = findViewById(R.id.amount);
        TextView dateTextView = findViewById(R.id.date);
        TextView createdByTextView = findViewById(R.id.user);

        remarkTextView.setText(remark);
        categoryTextView.setText(category);
        amountTextView.setText(amount + " " + currency);
        dateTextView.setText(date);
        createdByTextView.setText(createdBy);

        String amountText = amount + " " + currency;
        amountTextView.setText(amountText);


        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}