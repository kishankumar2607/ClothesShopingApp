package com.example.clothesshoppingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        submitButton = findViewById(R.id.submitButton);

        // Set listener
        submitButton.setOnClickListener(v -> handleForgotPassword());
    }

    private void handleForgotPassword() {
        String email = emailInput.getText().toString().trim();

        // Simple validation
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            emailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return;
        }

        // Simulate password reset request
        Toast.makeText(this, "Password reset instructions sent to " + email, Toast.LENGTH_SHORT).show();
        finish(); // Finish the forgot password activity and go back to login
    }
}
