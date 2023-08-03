package com.miwth.and102_asm.users;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.miwth.and102_asm.R;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity implements UserAuth {

    ImageButton btnBack;
    EditText edtEmail;
    Button btnSend;
    TextView tvWelcome2;
    LottieAnimationView animationView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtPhone);

        animationView = findViewById(R.id.animationView);

        btnSend = findViewById(R.id.btnSend);

        tvWelcome2 = findViewById(R.id.tvWelcome2);

        btnSend.setOnClickListener(v -> {
            btnSend.setText("");
            edtEmail.setEnabled(false);
            btnSend.setEnabled(false);
            animationView.playAnimation();
            animationView.setVisibility(View.VISIBLE);

            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                edtEmail.setError("Email is required!");
                edtEmail.requestFocus();
            } else if (!email.contains("@")) {
                edtEmail.setError("Invalid email!");
                edtEmail.requestFocus();
            } else {
                tvWelcome2.setText("If your email is registered, you will receive an Email to reset your password.");
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("email", "Email sent.");
                            } else {
                                Log.d("email", "Email not sent.");
                                Log.d("email", "Msg: " + Objects.requireNonNull(task.getException()).getMessage());
                            }
                        });
            }
        });
        btnBack.setOnClickListener(v -> onBackPressed());
    }
}