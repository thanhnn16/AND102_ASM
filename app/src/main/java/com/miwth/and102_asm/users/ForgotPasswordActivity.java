package com.miwth.and102_asm.users;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.miwth.and102_asm.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageButton btnBack;
    EditText edtEmail, edtOTP, edtNewPassword, edtConfirmPassword;
    Button btnSend, btnVerify, btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtPhone);
        edtOTP = findViewById(R.id.edtCode);

        btnSend = findViewById(R.id.btnSend);
        btnVerify = findViewById(R.id.btnVerify);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}