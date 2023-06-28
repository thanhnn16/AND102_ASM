package com.miwth.and102_asm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.miwth.and102_asm.fragment.AboutMeFragment;
import com.miwth.and102_asm.fragment.AccountFragment;
import com.miwth.and102_asm.fragment.ProductManagementFragment;
import com.miwth.and102_asm.fragment.SettingsFragment;
import com.miwth.and102_asm.users.LoginActivity;
import com.miwth.and102_asm.users.UserAuth;

public class MainActivity extends AppCompatActivity implements UserAuth {
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    TextView toolbarTitle;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbarTitle.setText("Account Management");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        Fragment defaultFragment = new AccountFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, defaultFragment)
                .commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_account) {
                    selectedFragment = new AccountFragment();
                    toolbarTitle.setText("Account");

                } else if (item.getItemId() == R.id.nav_prod_mgmt) {
                    selectedFragment = new ProductManagementFragment();
                    toolbarTitle.setText("Products Management");

                } else if (item.getItemId() == R.id.nav_about) {
                    selectedFragment = new AboutMeFragment();
                    toolbarTitle.setText("About Me");

                } else if (item.getItemId() == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    toolbarTitle.setText("Settings");

                } else if (item.getItemId() == R.id.nav_logout) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Logout");
                    builder.setIcon(R.drawable.baseline_logout_24);
                    builder.setMessage("Are you sure you want to logout?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        SharedPreferences sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return false;
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                drawerLayout.close();
                return true;
            }
        });

    }
}