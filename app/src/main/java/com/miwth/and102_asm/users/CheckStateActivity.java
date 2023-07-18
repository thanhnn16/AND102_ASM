package com.miwth.and102_asm.users;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miwth.and102_asm.MainActivity;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.welcome.LoginSignupScreen;

public class CheckStateActivity extends AppCompatActivity implements UserAuth {
    final int SPLASH_SCREEN_TIME = 2100;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_state);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
        Boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUserLoggedIn()) {
                    if (getUserEmail() != null && getDisplayName() == null) {
                        if (isLoggedIn) {
                            startMainActivity();
                        } else {
                            logout();
                            startLoginActivity();
                        }
                    } else startMainActivity();
                } else {
                    startLoginActivity();
                }
            }
        }, SPLASH_SCREEN_TIME);
    }

    private void startMainActivity() {
        startActivity(new Intent(CheckStateActivity.this, MainActivity.class));
        if (getDisplayName() == null && getUserEmail() == null) {
            sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
            String phoneNumber = sharedPreferences.getString("phoneNumber", "");
            Toast.makeText(CheckStateActivity.this, "Welcome back\n" + phoneNumber, Toast.LENGTH_SHORT).show();
        } else if (getDisplayName() == null && getUserEmail() != null) {
            Toast.makeText(CheckStateActivity.this, "Welcome back\n" + getUserEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CheckStateActivity.this, "Welcome back\n" + getDisplayName(), Toast.LENGTH_SHORT).show();
        }
        finishAffinity();
    }

    private void startLoginActivity() {
        startActivity(new Intent(CheckStateActivity.this, LoginSignupScreen.class));
        finishAffinity();
    }
}