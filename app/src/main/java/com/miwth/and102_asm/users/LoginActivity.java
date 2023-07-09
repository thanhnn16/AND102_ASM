package com.miwth.and102_asm.users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
    TextView tvGoogle;
    TextView tvOTP;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    ActivityResultLauncher<Intent> googleLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> googleTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = googleTask.getResult(ApiException.class);
                        Toast.makeText(this, "Logging...", Toast.LENGTH_SHORT).show();
                        if (googleTask.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");

                            // Lấy IdToken của tài khoản Google
                            String idToken = account.getIdToken();

                            if (idToken != null && !idToken.isEmpty()) {
                                // Tạo AuthCredential từ IdToken
                                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

                                // Đăng nhập vào Firebase với AuthCredential
                                mAuth.signInWithCredential(credential)
                                        .addOnCompleteListener(firebaseTask -> {
                                            if (firebaseTask.isSuccessful()) {
                                                // Đăng nhập thành công
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    mAuth.updateCurrentUser(user);
                                                } else {
                                                    Log.e("TAG", "User is null");
                                                }
                                                onLoginSuccess(account.getEmail());
                                            } else {
                                                // Đăng nhập thất bại
                                                Log.w("TAG", "signInWithCredential:failure", firebaseTask.getException());
                                            }
                                        });
                            } else {
                                Log.e("TAG", "IdToken is null or empty");
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", googleTask.getException());
                        }
                    } catch (ApiException e) {
                        Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                    }
                }
            });

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

        tvGoogle = findViewById(R.id.tvGoogle);
        tvOTP = findViewById(R.id.tvPhoneOTP);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

        tvGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleLogin.launch(signInIntent);
            }
        });


        tvOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PhoneLogin.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoginSuccess(String email) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        Toast.makeText(this, "Login succeed\nWelcome " + email, Toast.LENGTH_SHORT).show();
        finishAffinity();
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