package com.miwth.and102_asm.fragment;

public interface OnLoginListener {
    void onLoginSuccess(String email);
    void onLoginFailure(String errorMessage);
}
