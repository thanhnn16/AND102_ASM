package com.miwth.and102_asm.users;

import static android.util.Log.e;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.miwth.and102_asm.MainActivity;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.welcome.OnboardingActivity;

import java.io.File;

public class CheckStateActivity extends AppCompatActivity implements UserAuth {
    final int SPLASH_SCREEN_TIME = 1000;
    SharedPreferences sharedPreferences;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_state);
        user = mAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

//        check if getCacheDir() > 100MB then delete
        if (getCacheDir().getTotalSpace() > 100000000) {
            try {
                // get all files in the cache directory
                File[] files = getCacheDir().listFiles();
                if (files == null) {
                    return;
                }
                for (File file : files) {
                    file.delete();
                }
            } catch (Exception e) {
                // handle exception
                e("TAG", "try delete files: ", e);
            }
        }

        new Handler().postDelayed(() -> {
            if (user != null || isLoggedIn) {
                if (getDisplayName() == null && getUserEmail() == null) {
                    startSignUpActivity();
                    Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                } else {
                    startMainActivity();
                }
            } else {
                Log.d("TAG", "checkLogin: " + null);
                logout();
                startSignUpActivity();
            }

        }, SPLASH_SCREEN_TIME);
    }

    private void startMainActivity() {
        startActivity(new Intent(CheckStateActivity.this, MainActivity.class).setAction("START_MAIN_ACTIVITY"));
        Toast.makeText(CheckStateActivity.this, "Welcome back\n" + getDisplayName(), Toast.LENGTH_SHORT).show();
        finishAffinity();
    }

    private void startSignUpActivity() {
        startActivity(new Intent(CheckStateActivity.this, OnboardingActivity.class).setAction("START_SIGN_UP_ACTIVITY"));
        finishAffinity();
    }
}