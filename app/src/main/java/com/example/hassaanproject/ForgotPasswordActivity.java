package com.example.hassaanproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etResetEmail;
    private Button btnResetPassword;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        etResetEmail = findViewById(R.id.etResetEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnResetPassword.setOnClickListener(v -> sendResetLink());

        tvBackToLogin.setOnClickListener(v -> finish()); // Go back to the previous screen (Login)
    }

    private void sendResetLink() {
        String email = etResetEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real app, you would add logic here to interact with a backend service
        // to actually send a password reset email.
        Toast.makeText(this, "If an account exists, a reset link has been sent to " + email, Toast.LENGTH_LONG).show();

        // Finish this activity and return to the login screen
        finish();
    }
}
