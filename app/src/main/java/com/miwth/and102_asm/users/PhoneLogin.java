package com.miwth.and102_asm.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
    private boolean isPhoneNumberExists = false;

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

        tvGoBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString();

            if (phone.isEmpty()) {
                edtPhone.setError("Please enter your phone number");
                edtPhone.requestFocus();
            } else if (phone.length() < 9) {
                edtPhone.setError("Please enter a valid phone number");
                edtPhone.requestFocus();
            } else if (checkForPhoneNumber(phone)) {
                Log.i("PhoneLogin", "onCreate: " + isPhoneNumberExists);
                Log.i("PhoneLogin", "onCreate: " + phone);
                edtPhone.setError("This phone number is already registered");
                edtPhone.requestFocus();

            } else {
                edtCode.setError(null);
                edtCode.setEnabled(true);
                edtCode.requestFocus();

                btnVerify.setEnabled(true);
                btnVerify.setBackgroundColor(getResources().getColor(R.color.send_bg_enable, null));
                btnVerify.setTextColor(getResources().getColor(R.color.send_enable, null));

                startPhoneNumberVerification(phone);
                ResendOtpCountDown countDownTimer = new ResendOtpCountDown(60000, 1000);
                countDownTimer.start();
            }
        });

        btnVerify.setOnClickListener(v -> {
            String code = edtCode.getText().toString();
            if (code.isEmpty()) {
                edtCode.setError("Please enter your code");
                edtCode.requestFocus();
            } else if (code.length() < 6) {
                edtCode.setError("Please enter a valid code");
                edtCode.requestFocus();
            } else {
                verifyPhoneNumberWithCode(mVerificationId, code);
                startButtonAnimation();
                new Handler().postDelayed(this::stopButtonAnimation, 2000);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Sign in with the credential
                // ...
                signInWithPhoneAuthCredential(credential);
                edtCode.setText(credential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneLogin.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
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
                        if (task.getResult().getUser() != null) {
                            // Sign in success
                            Toast.makeText(this, "Phone number verified", Toast.LENGTH_SHORT).show();
                            String phoneNumber = edtPhone.getText().toString();
                            DatabaseReference ref = infoDB.child("phone");
                            ref.child(phoneNumber).setValue(phoneNumber);
                            Intent newIntent = new Intent(PhoneLogin.this, MainActivity.class);
                            FirebaseUser user = task.getResult().getUser();
                            mAuth.updateCurrentUser(user);
                            startActivity(newIntent);
                            finishAffinity();
                        }
                    } else {
                        if (task.getException() != null)
                            Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        // Sign in failed, display a message and update the UI
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    Phương thức countdown gây ra độ trễ khá lớn

    /*@SuppressLint("SetTextI18n")
    public void countDown() {
        new Thread(() -> {
            for (int i = 60; i >= 0; i--) {
                try {
                    Thread.sleep(1000);
                    final int finalI = i;
                    runOnUiThread(() -> {
                        btnSend.setText("Resend OTP in " + finalI + "s");
                        btnSend.setBackgroundColor(getResources().getColor(R.color.gray_light, null));
                        btnSend.setEnabled(false);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> {
                btnSend.setText("Resend OTP");
                btnSend.setBackgroundColor(getResources().getColor(R.color.btn_login, null));
                btnSend.setEnabled(true);
            });
        }).start();
    }*/

    public void startButtonAnimation() {
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
        btnVerify.setText("");
    }

    @SuppressLint("SetTextI18n")
    public void stopButtonAnimation() {
        lottieAnimationView.setVisibility(View.GONE);
        lottieAnimationView.pauseAnimation();
        btnVerify.setText("Verify");
    }

    private boolean checkForPhoneNumber(String number) {
        infoDB.orderByChild("phone").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isPhoneNumberExists = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return isPhoneNumberExists;
    }

    public class ResendOtpCountDown extends CountDownTimer {

        public ResendOtpCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long seconds = millisUntilFinished / 1000;
            btnSend.setText(getString(R.string.resend_otp_in) + seconds + getString(R.string.s_second));
            btnSend.setBackgroundColor(getResources().getColor(R.color.send_bg_disable, getTheme()));
            btnSend.setTextColor(getResources().getColor(R.color.send_disable, getTheme()));
            btnSend.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btnSend.setText(R.string.resend_otp);
            btnSend.setBackgroundColor(getResources().getColor(R.color.send_bg_enable, getTheme()));
            btnSend.setTextColor(getResources().getColor(R.color.send_enable, getTheme()));
            btnSend.setEnabled(true);
        }
    }
}