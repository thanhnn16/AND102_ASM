package com.miwth.and102_asm.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.miwth.and102_asm.R;
import com.miwth.and102_asm.users.SignUpActivity;

public class GetStarted extends AppCompatActivity {
    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        btnGetStarted = findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(v ->
                startActivity(
                        new Intent(GetStarted.this, SignUpActivity.class)));
    }
}