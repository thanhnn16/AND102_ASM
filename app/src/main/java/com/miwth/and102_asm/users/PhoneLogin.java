package com.miwth.and102_asm.users;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.miwth.and102_asm.MainActivity;
import com.miwth.and102_asm.R;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity implements UserAuth {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    EditText edtPhone, edtCode;
    Button btnSend, btnVerify;
    ImageButton tvGoBack;
    LottieAnimationView lottieAnimationView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        edtPhone = findViewById(R.id.edtPhone);
        edtCode = findViewById(R.id.edtCode);
        tvGoBack = findViewById(R.id.tvGoBack);

        btnSend = findViewById(R.id.btnSend);
        btnVerify = findViewById(R.id.btnVerify);

        lottieAnimationView = findViewById(R.id.animationView);

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edtPhone.getText().toString();
                if (phone.isEmpty()) {
                    edtPhone.setError("Please enter your phone number");
                    edtPhone.requestFocus();
                }
                else if (phone.length() < 9) {
                    edtPhone.setError("Please enter a valid phone number");
                    edtPhone.requestFocus();
                }
                else {
                    countDown();
                    edtCode.setError(null);
                    btnVerify.setEnabled(true);
                    edtCode.setEnabled(true);
                    edtCode.requestFocus();
                    btnVerify.setBackgroundColor(getResources().getColor(R.color.btn_login, null));
                    btnVerify.setTextColor(getResources().getColor(R.color.white, null));
                    startPhoneNumberVerification(phone);
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edtCode.getText().toString();
                if (code.isEmpty()) {
                    edtCode.setError("Please enter your code");
                    edtCode.requestFocus();
                }
                else if (code.length() < 6) {
                    edtCode.setError("Please enter a valid code");
                    edtCode.requestFocus();
                }
                else {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                    startButtonAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopButtonAnimation();
                        }
                    }, 2000);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Sign in with the credential
                // ...
                edtCode.setText(credential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(PhoneLogin.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save the verification id somewhere
                mVerificationId = verificationId;
                // The corresponding whitelisted code above should be used to complete sign-in.
                // PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                // ...
            }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+84" + phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        // [END verify_with_code]
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Toast.makeText(this, "Logged in with OTP", Toast.LENGTH_SHORT).show();
                        Intent newIntent = new Intent(PhoneLogin.this, MainActivity.class);
                        sharedPreferences = getSharedPreferences("login_state", MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putString("phoneNumber", edtPhone.getText().toString());
                        editor.apply();
//                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(newIntent);
//                        finish();
                        finishAffinity();
                    } else {
                        if (task.getException() != null)
                            Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void countDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 60; i >= 0; i--) {
                    try {
                        Thread.sleep(1000);
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnSend.setText("Resend OTP in " + finalI + "s");
                                btnSend.setEnabled(false);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSend.setText("Resend OTP");
                        btnSend.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    public void startButtonAnimation() {
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
        btnVerify.setText("");
    }

    public void stopButtonAnimation() {
        lottieAnimationView.setVisibility(View.GONE);
        lottieAnimationView.pauseAnimation();
        btnVerify.setText("Verify");
    }
}