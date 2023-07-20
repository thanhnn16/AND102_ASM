package com.miwth.and102_asm.users;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        new Handler().postDelayed(() -> {
            if (isUserLoggedIn()) {
                if (getUserEmail() != null) {
                    if (isLoggedIn) {
                        startMainActivity();
                    } else {
                        logout();
                        startLoginActivity();
                    }
                } else if (getUserEmail() == null) {
                    startSignUpActivity();
                    Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                }
            } else {
                startLoginActivity();
            }
        }, SPLASH_SCREEN_TIME);
    }

    private void startMainActivity() {
        startActivity(new Intent(CheckStateActivity.this, MainActivity.class));
        Toast.makeText(CheckStateActivity.this, "Welcome back\n" + getDisplayName(), Toast.LENGTH_SHORT).show();
        finishAffinity();
    }

    private void startLoginActivity() {
        startActivity(new Intent(CheckStateActivity.this, LoginSignupScreen.class));
        finishAffinity();
    }

    private void startSignUpActivity() {
        startActivity(new Intent(CheckStateActivity.this, SignUpActivity.class));
        finishAffinity();
    }
}