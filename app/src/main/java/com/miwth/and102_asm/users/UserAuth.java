package com.miwth.and102_asm.users;

import com.google.firebase.auth.FirebaseAuth;

public interface UserAuth {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    default boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    default String getUID() {
        return mAuth.getCurrentUser().getUid();
    }

    default String getUserEmail() {
        return mAuth.getCurrentUser().getEmail();
    }

    default void logout() {
        mAuth.signOut();
    }

}
