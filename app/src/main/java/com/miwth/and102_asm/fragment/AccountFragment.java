package com.miwth.and102_asm.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.users.LoginActivity;
import com.miwth.and102_asm.users.UserAuth;

import java.util.Objects;

public class AccountFragment extends Fragment implements UserAuth {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout btnEditProfile, btnChangePassword, btnLogout, btnExit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ImageView ivProfile = view.findViewById(R.id.imageView_profile);
        TextView tvName = view.findViewById(R.id.userName);

        sharedPreferences = requireActivity().getSharedPreferences("login_state", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnEditProfile = view.findViewById(R.id.updateInfo);
        btnChangePassword = view.findViewById(R.id.changePassword);
        btnLogout = view.findViewById(R.id.logout);
        btnExit = view.findViewById(R.id.exitApp);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() == null && user.getEmail() == null) {
                String phoneNumber = sharedPreferences.getString("phoneNumber", "");
                tvName.setText(phoneNumber);
            } else if (user.getDisplayName() == null && user.getEmail() != null) {
                tvName.setText(user.getEmail());
            } else {
                tvName.setText(user.getDisplayName());
            }
            if (user.getPhotoUrl() != null) {
                String photoUrlString = Objects.requireNonNull(user.getPhotoUrl()).toString();
                Glide.with(this).load(photoUrlString).fitCenter().into(ivProfile);
                Log.i("photoUrl", photoUrlString);
            }
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                build new confirm dialog to confirm log out
                Log.i("logout", "logout");
                logout();
                editor.clear();
                editor.apply();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finishAffinity();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finishAffinity();
            }
        });

        return view;
    }
}