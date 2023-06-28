package com.miwth.and102_asm.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.miwth.and102_asm.R;

public class SignUpTabFragment extends Fragment {

    FirebaseAuth mAuth;
    LottieAnimationView buttonAnimation;
    LinearLayout btnSignUp;
    TextView tvSignUp;
    private OnSignUpListener onSignUpListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View signUpTabFragment = inflater.inflate(R.layout.signup_tab_fragment, container, false);

        TextInputEditText edtEmail = signUpTabFragment.findViewById(R.id.edtEmail);
        TextInputEditText edtPassword = signUpTabFragment.findViewById(R.id.edtPassword);
        TextInputEditText edtConfirmPassword = signUpTabFragment.findViewById(R.id.edtConfirmPassword);
        btnSignUp = signUpTabFragment.findViewById(R.id.btnSignUp);
        buttonAnimation = signUpTabFragment.findViewById(R.id.button_animation);
        tvSignUp = signUpTabFragment.findViewById(R.id.tvSignUp);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String confirmPassword = edtConfirmPassword.getText().toString();

                startButtonAnimation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetButton();
                    }
                }, 2000);

                if (email.isEmpty()) {
                    edtEmail.setError("Email is empty");
                    edtEmail.requestFocus();
                } else if (password.isEmpty()) {
                    edtEmail.setError("Password is empty");
                    edtPassword.requestFocus();
                } else if (confirmPassword.isEmpty()) {
                    edtEmail.setError("Confirm password is empty");
                    edtConfirmPassword.requestFocus();
                } else {
                    if (!password.equals(confirmPassword)) {
                        edtConfirmPassword.setError("Password and confirm password are not the same");
                        edtConfirmPassword.requestFocus();
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        onSignUpListener.onSignUpSuccess(email);
                                    } else {
                                        onSignUpListener.onSignUpFailure("Sign up failed, please try again");
                                    }
                                });
                    }
                }
            }
        });
        return signUpTabFragment;
    }

    public void setOnSignUpListener(OnSignUpListener listener) {
        this.onSignUpListener = listener;
    }

    public void resetButton() {
        buttonAnimation.setVisibility(View.GONE);
        buttonAnimation.cancelAnimation();
        tvSignUp.setVisibility(View.VISIBLE);
    }

    public void startButtonAnimation() {
        buttonAnimation.setVisibility(View.VISIBLE);
        buttonAnimation.playAnimation();
        tvSignUp.setVisibility(View.GONE);
    }
}
