package com.miwth.and102_asm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.miwth.and102_asm.fragment.AboutMeFragment;
import com.miwth.and102_asm.fragment.AccountFragment;
import com.miwth.and102_asm.fragment.ProductManagementFragment;
import com.miwth.and102_asm.fragment.SettingsFragment;
import com.miwth.and102_asm.fragment.YoutubeFragment;
import com.miwth.and102_asm.users.UserAuth;
import com.miwth.and102_asm.welcome.LoginSignupScreen;

import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAuth {
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    TextView toolbarTitle;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbarTitle.setText("Account Management");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.btn_login, getTheme()));
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        Fragment defaultFragment = new ProductManagementFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, defaultFragment)
                .commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    toolbarTitle.setText("Settings");

                } else if (itemId == R.id.nav_admin) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:0346542636"));
                    startActivity(dialIntent);

                } else if (itemId == R.id.nav_email) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nongnguyenthanh.0106@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Message");
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));

                } else if (itemId == R.id.nav_logout) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Logout");
                    builder.setIcon(R.drawable.baseline_logout_24);
                    builder.setMessage("Are you sure you want to logout?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        logout();
                        startActivity(new Intent(MainActivity.this, LoginSignupScreen.class));
                        SharedPreferences sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    drawerLayout.close();
                    return true;
                }

                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(itemId));

                if (fragment == null) {
                    // Nếu fragment chưa tồn tại trong back stack, tạo mới và thêm vào stack
                    if (itemId == R.id.product_management) {
                        fragment = new ProductManagementFragment();
                    } else if (itemId == R.id.youtube) {
                        fragment = new YoutubeFragment();
                    } else if (itemId == R.id.contact) {
                        fragment = new AboutMeFragment();
                    } else if (itemId == R.id.account_management) {
                        fragment = new AccountFragment();
                    } else {
                        fragment = new SettingsFragment();
                    }
                    fragmentTransaction.add(R.id.fragment_container, fragment, String.valueOf(itemId));
                } else {
                    // Nếu fragment đã tồn tại trong back stack, chỉ hiển thị lại
                    fragmentTransaction.show(fragment);
                }

                // Ẩn các fragment khác (nếu có)
                List<Fragment> fragments = fragmentManager.getFragments();
                for (Fragment frag : fragments) {
                    if (frag != fragment) {
                        fragmentTransaction.hide(frag);
                    }
                }

                fragmentTransaction.commit();
                return true;
            }
        });

        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Toast.makeText(MainActivity.this, "Reselected", Toast.LENGTH_SHORT).show();
            }
        });
    }


}