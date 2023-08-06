package com.miwth.and102_asm.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseUser;
import com.miwth.and102_asm.R;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity implements UserAuth {
    ImageButton btnBack;
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    LinearLayout btnChangePassword;
    TextView btnText;
    LottieAnimationView lottieAnimationView;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        user = mAuth.getCurrentUser();
        btnBack = findViewById(R.id.tvGoBack);
        edtOldPassword = findViewById(R.id.etOldPassword);
        edtNewPassword = findViewById(R.id.etNewPassword);
        edtConfirmPassword = findViewById(R.id.etRePassword);
        btnChangePassword = findViewById(R.id.btnChange);
        btnText = findViewById(R.id.btnText);
        lottieAnimationView = findViewById(R.id.btnAnimation);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnChangePassword.setOnClickListener(v -> {
            setChangePassword();
        });

    }

    private void setChangePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (oldPassword.isEmpty()) {
            edtOldPassword.setError("Old password is required!");
            edtOldPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            edtNewPassword.setError("New password is required!");
            edtNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Confirm password is required!");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            edtConfirmPassword.setError("Confirm password is not match!");
            edtConfirmPassword.requestFocus();
            return;
        }
        playAnimation();
        String email = user.getEmail();
//        Login using firebase
        if (email != null) {
            mAuth.signInWithEmailAndPassword(email, oldPassword).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    stopAnimation();
                    edtOldPassword.setError("Old password is incorrect!");
                    edtOldPassword.requestFocus();
                }
            });
        }

        user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                stopAnimation();
                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully! Re-login to continue!", Toast.LENGTH_LONG).show();
                logout();
                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class).setAction("changePassword"));
                finishAffinity();
            } else {
                stopAnimation();
                edtOldPassword.setError(Objects.requireNonNull(task.getException()).getMessage());
                edtOldPassword.requestFocus();
                Toast.makeText(ChangePasswordActivity.this, "Failed to change password! Try again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void playAnimation() {
        btnText.setVisibility(View.GONE);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
    }

    public void stopAnimation() {
        lottieAnimationView.setVisibility(View.GONE);
        lottieAnimationView.cancelAnimation();
        btnText.setVisibility(View.VISIBLE);
    }

}