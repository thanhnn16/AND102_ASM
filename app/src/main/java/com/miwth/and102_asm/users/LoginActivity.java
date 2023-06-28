package com.miwth.and102_asm.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.miwth.and102_asm.MainActivity;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.LoginAdapter;
import com.miwth.and102_asm.fragment.OnLoginListener;
import com.miwth.and102_asm.fragment.OnSignUpListener;

public class LoginActivity extends AppCompatActivity implements OnLoginListener, OnSignUpListener {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    LoginAdapter loginAdapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        loginAdapter = new LoginAdapter(getSupportFragmentManager(), this.getLifecycle(), this, this);
        viewPager.setAdapter(loginAdapter);

//        chọn page khi chuyển tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tabSelected) {
                viewPager.setCurrentItem(tabSelected.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tabUnselected) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tabReselected) {

            }
        });

//        chọn tab khi chuyển page
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


    }

    @Override
    public void onLoginSuccess(String email) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        Toast.makeText(this, "Login succeed\nWelcome " + email, Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onLoginFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSignUpSuccess(String email) {
        Toast.makeText(this, "Sign up succeed\nWelcome " + email, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onSignUpFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}