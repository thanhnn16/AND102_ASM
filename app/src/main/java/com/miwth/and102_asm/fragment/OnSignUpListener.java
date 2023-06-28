package com.miwth.and102_asm.fragment;

public interface OnSignUpListener {
    void onSignUpSuccess(String email);
    void onSignUpFailure(String message);
}
