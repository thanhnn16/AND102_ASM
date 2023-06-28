package com.miwth.and102_asm.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.miwth.and102_asm.fragment.LoginTabFragment;
import com.miwth.and102_asm.fragment.OnLoginListener;
import com.miwth.and102_asm.fragment.OnSignUpListener;
import com.miwth.and102_asm.fragment.SignUpTabFragment;
import com.miwth.and102_asm.users.LoginActivity;

public class LoginAdapter extends FragmentStateAdapter {
    Context context;
    final int TOTAL_TABS = 2;
    private OnLoginListener onLoginListener;
    private OnSignUpListener onSignUpListener;

    public LoginAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, OnLoginListener onLoginListener, OnSignUpListener onSignUpListener) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.onLoginListener = onLoginListener;
        this.onSignUpListener = onSignUpListener;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                loginTabFragment.setOnLoginListener(onLoginListener);
                return loginTabFragment;
            case 1:
                SignUpTabFragment signUpTabFragment = new SignUpTabFragment();
                signUpTabFragment.setOnSignUpListener(onSignUpListener);
                return signUpTabFragment;
            default:
                return null;
        }
    }
    @Override
    public int getItemCount() {
        return TOTAL_TABS;
    }
}
