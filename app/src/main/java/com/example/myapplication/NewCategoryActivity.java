package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Category;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewCategoryActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_category);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        TextView back_btn = findViewById(R.id.back_btn);
        EditText editTextText = findViewById(R.id.editTextText);
        Button button3 = findViewById(R.id.button3);

        back_btn.setOnClickListener(v -> finish());
        button3.setOnClickListener(v -> {
            String categoryName = editTextText.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                addNewCategory(categoryName);
            } else {
                Toast.makeText(NewCategoryActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addNewCategory(String categoryName) {
        executorService.execute(() -> {
            Category category = new Category();
            category.name = categoryName;
            appDatabase.categoryDao().insert(category);

            runOnUiThread(() -> {
                Toast.makeText(NewCategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newCategory", categoryName);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        });
    }
}
