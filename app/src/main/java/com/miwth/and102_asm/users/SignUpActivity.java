package com.miwth.and102_asm.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.miwth.and102_asm.R;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements UserAuth {
    EditText edtDisplayName, edtEmail, edtPassword, edtRePW;
    Button btnSignUp;
    CheckBox chkAcceptTerm;
    FirebaseUser user;
    ImageButton tvGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtDisplayName = findViewById(R.id.etDisplayName);
        edtEmail = findViewById(R.id.etEmail);
        edtPassword = findViewById(R.id.etPassword);
        edtRePW = findViewById(R.id.etRePW);
        tvGoBack = findViewById(R.id.tvGoBack);

        btnSignUp = findViewById(R.id.btnSignUp);
        chkAcceptTerm = findViewById(R.id.chkAcceptTerm);

        btnSignUp.setOnClickListener(v -> setBtnSignUp());
        tvGoBack.setOnClickListener(v -> finish());
    }

    private void setBtnSignUp() {
        String displayName = edtDisplayName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pw = edtPassword.getText().toString();
        String rePw = edtRePW.getText().toString();


        if (displayName.isEmpty()) {
            edtDisplayName.requestFocus();
            edtDisplayName.setError("Please enter display name");
        } else if (email.isEmpty()) {
            edtEmail.requestFocus();
            edtEmail.setError("Please enter email");
        } else if (!checkValidEmail(email)) {
            edtEmail.requestFocus();
            edtEmail.setError("Please enter a valid email");
        } else if (pw.isEmpty()) {
            edtPassword.requestFocus();
            edtPassword.setError("Please enter password");
        } else if (rePw.isEmpty()) {
            edtRePW.requestFocus();
            edtRePW.setError("Please enter confirm password");
        } else if (!rePw.equals(pw)) {
            edtRePW.requestFocus();
            edtRePW.setError("Confirm password is not match");
        } else if (!chkAcceptTerm.isChecked()) {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
        } else if (!checkStrongPassword(pw)) {
            edtPassword.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = task.getResult().getUser();
                    mAuth.updateCurrentUser(user);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build();
                    user.updateProfile(profileUpdates);
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                }
            });
            showLoading();
            startActivity(new Intent(SignUpActivity.this, ChangeUserPictureActivity.class));
        }
    }

    private boolean checkValidEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(email).matches();
    }

    private boolean checkStrongPassword(String password) {
        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            return false;

        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            edtPassword.setError("Password must contain at least 1 number");
            return false;

        } else if (!Pattern.compile("[a-z]").matcher(password).find()) {
            edtPassword.setError("Password must contain at least 1 lowercase letter");
            return false;

        } else if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            edtPassword.setError("Password must contain at least 1 uppercase letter");
            return false;

        } else if (!Pattern.compile("[!@#$%^&*()_+=|<>?{}\\[\\]~-]").matcher(password).find()) {
            edtPassword.setError("Password must contain at least 1 special character");
            return false;

        } else {
            Toast.makeText(this, "Sign up successfully", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @SuppressLint("InflateParams")
    private void showLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(LayoutInflater.from(this).inflate(
                R.layout.loading_after_update, null));
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        new Handler().postDelayed(dialog::dismiss, 3000);
    }
}