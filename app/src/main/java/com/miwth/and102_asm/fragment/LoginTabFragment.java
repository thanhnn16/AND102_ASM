package com.miwth.and102_asm.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.users.ForgotPasswordActivity;
import com.miwth.and102_asm.users.UserAuth;

public class LoginTabFragment extends Fragment implements UserAuth {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LottieAnimationView buttonAnimation;
    LinearLayout btnLogin;
    TextView tvLogin, tvForgotPassword;
    private OnLoginListener onLoginListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loginTabFragment = inflater.inflate(R.layout.login_tab_fragment, container, true);

        TextInputEditText etEmail = loginTabFragment.findViewById(R.id.edtEmail);
        TextInputEditText etPassword = loginTabFragment.findViewById(R.id.edtPassword);
        CheckBox cbRememberMe = loginTabFragment.findViewById(R.id.cbRememberMe);
        tvForgotPassword = loginTabFragment.findViewById(R.id.tvForgotPassword);
        btnLogin = loginTabFragment.findViewById(R.id.btnLogin);
        tvLogin = loginTabFragment.findViewById(R.id.tvLogin);
        buttonAnimation = loginTabFragment.findViewById(R.id.button_animation);

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                boolean rememberMe = cbRememberMe.isChecked();

                startButtonAnimation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetButton();
                    }
                }, 2000);

                if (email.isEmpty()) {
                    etEmail.setError("Email is empty");
                    etEmail.requestFocus();
                } else if (password.isEmpty()) {
                    etPassword.setError("Password is empty");
                    etPassword.requestFocus();
                } else {
                    if (rememberMe) {
                        sharedPreferences = getActivity().getSharedPreferences("login_state", getActivity().MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("is_logged_in", true);
                        editor.apply();
                    }
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    onLoginListener.onLoginSuccess(email);
                                } else {
                                    onLoginListener.onLoginFailure("Wrong email or password");
                                    etEmail.requestFocus();
                                }
                            });
                }
            }
        });

        return loginTabFragment;
    }

    public void setOnLoginListener(OnLoginListener listener) {
        this.onLoginListener = listener;
    }

    public void resetButton() {
        buttonAnimation.setVisibility(View.GONE);
        buttonAnimation.cancelAnimation();
        tvLogin.setVisibility(View.VISIBLE);
    }

    public void startButtonAnimation() {
        buttonAnimation.setVisibility(View.VISIBLE);
        buttonAnimation.playAnimation();
        tvLogin.setVisibility(View.GONE);
    }
}

