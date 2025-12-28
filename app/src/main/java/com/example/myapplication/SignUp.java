package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        EditText emailET = findViewById(R.id.editTextEmail);
        EditText passwordET = findViewById(R.id.editTextPassword);


        TextView back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());

        Button SignUp_btn = findViewById(R.id.SignUp_btn);
        SignUp_btn.setOnClickListener(view -> {
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Sign up " + email + " " + password, Toast.LENGTH_SHORT).show();

            createAccount(email, password);
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserInformation(firebaseUser.getUid(), email, password);
                        }
                        Toast.makeText(SignUp.this, "Sign up successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUp.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("Authentication", task.getException().toString());
                        finish();
                    }
                });
    }
    private void saveUserInformation(String userId, String email, String pass) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("pass", pass);
        user.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUp.this, "Sign up successful.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(SignUp.this, "Error saving user details.", Toast.LENGTH_SHORT).show());
    }
}