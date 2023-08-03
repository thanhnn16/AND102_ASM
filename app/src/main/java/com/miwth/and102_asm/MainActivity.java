package com.miwth.and102_asm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.miwth.and102_asm.database.AddProductActivity;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.fragment.AccountFragment;
import com.miwth.and102_asm.fragment.CategoryFragment;
import com.miwth.and102_asm.fragment.ProductHomeFragment;
import com.miwth.and102_asm.fragment.SettingsFragment;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.SignUpActivity;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements UserAuth, ProductDAO {
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    TextView toolbarTitle;
    SmoothBottomBar bottomNavigationView;
    ImageButton toggleButton;
    ShapeableImageView profileImage;
    FirebaseUser user;
    BadgeDrawable badgeDrawable;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = mAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggleButton = findViewById(R.id.toggleButton);
        profileImage = findViewById(R.id.profile_image);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Glide.with(this).load(user.getPhotoUrl()).into(profileImage);
        profileImage.setOnClickListener(v -> {
            bottomNavigationView.setItemActiveIndex(3);
            bottomNavigationView.setSelected(true);
            Fragment selectedFragment = new AccountFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            drawerLayout.close();
        });


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        toggleButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Fragment defaultFragment = new ProductHomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, defaultFragment)
                .commit();

        navigationView.setNavigationItemSelectedListener(item -> {
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
                    startActivity(new Intent(MainActivity.this, SignUpActivity.class).setAction(Intent.ACTION_VIEW));
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
        });

        /*bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(itemId));
                if (fragment == null) {
                    if (itemId == R.id.product_home) {
                        fragment = new ProductHomeFragment();
                    } else if (itemId == R.id.category_menu) {
                        fragment = new CategoryFragment();
                    } else if (itemId == R.id.product_management) {
                        fragment = new ManagementFragment();
                    } else if (itemId == R.id.account_management) {
                        fragment = new AccountFragment();
                    }
                    fragmentTransaction.add(R.id.fragment_container, fragment, String.valueOf(itemId));
                } else {
                    fragmentTransaction.show(fragment);
                }

                List<Fragment> fragments = fragmentManager.getFragments();
                for (Fragment frag : fragments) {
                    if (frag != fragment) {
                        fragmentTransaction.hide(frag);
                    }
                }
                fragmentTransaction.commit();
                return true;
            }
        });*/

        /*bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Toast.makeText(MainActivity.this, "Reselected", Toast.LENGTH_SHORT).show();
            }
        });*/

        bottomNavigationView.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new ProductHomeFragment();
            if (i == 0) {
                fragment = new ProductHomeFragment();
            } else if (i == 1) {
                fragment = new CategoryFragment();
            } else if (i == 2) {
                showBottomDialog();
                return true;
            } else if (i == 3) {
                fragment = new AccountFragment();
            }
            fragmentTransaction.add(R.id.fragment_container, fragment, String.valueOf(i));
            fragmentTransaction.show(fragment);
            List<Fragment> fragments = fragmentManager.getFragments();
            for (
                    Fragment frag : fragments) {
                if (frag != fragment) {
                    fragmentTransaction.hide(frag);
                }
            }
            fragmentTransaction.commit();
            return true;
        });

        bottomNavigationView.setOnItemReselected(integer -> {
            if (integer == 2) {
                showBottomDialog();
            }
            return null;
        });
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        ImageView close = dialog.findViewById(R.id.close);
        TextView addProduct = dialog.findViewById(R.id.add_product);
        TextView addCategory = dialog.findViewById(R.id.add_category);
        close.setOnClickListener(view -> dialog.dismiss());

        ArrayList<Product> productArrayList = new ArrayList<>();

        mDatabase.child(getUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                productArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                }
            } else {
                Log.e("ProductManagement", "Error getting data", task.getException());
            }
        });

        addProduct.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class).setAction("AddProduct");
            intent.putParcelableArrayListExtra("productArrayList", productArrayList);
            startActivity(intent);
            dialog.dismiss();
        });

        addCategory.setOnClickListener(view -> {
            Toast.makeText(this, "Add category is under development", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}