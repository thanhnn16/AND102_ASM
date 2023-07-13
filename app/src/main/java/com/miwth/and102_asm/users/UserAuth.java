package com.miwth.and102_asm.users;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public interface UserAuth {
    DatabaseReference infoDB = FirebaseDatabase.getInstance("https://and102-asm-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference()
            .child("user_info");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    default boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    default String getUID() {
        return (mAuth.getCurrentUser()).getUid();
    }

    default String getUserEmail() {
        return (mAuth.getCurrentUser()).getEmail();
    }

    default String getDisplayName() {
        return (mAuth.getCurrentUser()).getDisplayName();
    }


    default void getBirthdayAndBio(Callback callback) {
        infoDB.child(getUID()).child("birthday").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        /*if (task.isSuccessful() && task.getResult().getValue() != null) {
                            String birthday = task.getResult().getValue().toString();
                            callback.onBirthdayLoaded(birthday);
                        } else {
                            String birthday = "";
                            callback.onBirthdayLoaded(birthday);
                            Log.d("TAG", task.getException().toString());
                        }*/
                        DataSnapshot birthdaySnapshot = task.getResult();
                        if (birthdaySnapshot != null && birthdaySnapshot.getValue() != null) {
                            String birthday = birthdaySnapshot.getValue().toString();
                            callback.onBirthdayLoaded(birthday);
                        }
                    }
                }
        );
        infoDB.child(getUID()).child("bio").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        /*if (task.isSuccessful() && task.getResult().getValue() != null) {
                            String bio = task.getResult().getValue().toString();
                            callback.onBioLoaded(bio);
                        } else {
                            String bio = "";
                            callback.onBioLoaded(bio);
                        }*/
                        DataSnapshot birthdaySnapshot = task.getResult();
                        if (birthdaySnapshot != null && birthdaySnapshot.getValue() != null) {
                            String bio = birthdaySnapshot.getValue().toString();
                            callback.onBioLoaded(bio);
                        }
                    }
                }
        );
    }

    default void updateProfile(FirebaseUser user, String displayName, String birthday, String bio, Uri photoUrl, Context context) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUrl)
                .build();
        infoDB.child(user.getUid()).child("birthday").setValue(birthday);
        infoDB.child(user.getUid()).child("bio").setValue(bio);

        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User profile updated.");
                            Toast.makeText(context, "User profile updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    default void logout() {
        mAuth.signOut();
    }

    public interface Callback {
        void onBirthdayLoaded(String birthday);

        void onBioLoaded(String bio);
    }
}
