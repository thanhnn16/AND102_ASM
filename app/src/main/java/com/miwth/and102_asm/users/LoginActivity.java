package com.miwth.and102_asm.users;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.miwth.and102_asm.MainActivity;
import com.miwth.and102_asm.R;

public class LoginActivity extends AppCompatActivity implements UserAuth {
    TextView tvGoogle, tvOTP, btnText;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    EditText etEmail, etPassword;
    LinearLayout btnLogin;
    ImageButton btnBack;
    CheckBox cbRememberMe;
    LottieAnimationView btnAnimation;
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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.tvGoBack);
        cbRememberMe = findViewById(R.id.chkRememberMe);
        btnAnimation = findViewById(R.id.btnAnimation);
        btnText = findViewById(R.id.btnText);

        tvGoogle = findViewById(R.id.tvGoogle);
        tvOTP = findViewById(R.id.tvPhoneOTP);

        btnLogin.setOnClickListener(v -> {
            startAnimation();
            LoginActivity.this.setButtonLogin();
            new Handler().postDelayed(this::stopAnimation, 2000);
        });
        btnBack.setOnClickListener(v -> finish());

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        tvGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleLogin.launch(signInIntent);
        });

        tvOTP.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PhoneLogin.class);
            startActivity(intent);
        });
    }

    private void setButtonLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        boolean rememberMe = cbRememberMe.isChecked();


        if (email.isEmpty()) {
            etEmail.setError("Email is empty");
            etEmail.requestFocus();
        } else if (password.isEmpty()) {
            etPassword.setError("Password is empty");
            etPassword.requestFocus();
        } else {
            if (rememberMe) {
                sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", true);
                editor.apply();
            }
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            etEmail.setError("Email or password is incorrect");
                            etEmail.requestFocus();
                        }
                    });
        }
    }

    private void startAnimation() {
        btnAnimation.setVisibility(View.VISIBLE);
        btnText.setVisibility(View.GONE);
        btnAnimation.playAnimation();
    }

    private void stopAnimation() {
        btnAnimation.setVisibility(View.GONE);
        btnText.setVisibility(View.VISIBLE);
        btnAnimation.pauseAnimation();
    }
}