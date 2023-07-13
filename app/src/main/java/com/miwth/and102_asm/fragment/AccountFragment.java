package com.miwth.and102_asm.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.users.LoginActivity;
import com.miwth.and102_asm.users.UpdateAccountInfo;
import com.miwth.and102_asm.users.UserAuth;

import java.util.Objects;

public class AccountFragment extends Fragment implements UserAuth, ProductDAO {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RelativeLayout btnProfileDetail, btnEditProfile, btnChangePassword, btnLogout, btnExit;
    FirebaseUser user;

    ActivityResultLauncher<Intent> updateInfo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        ProgressBar progressBar = new ProgressBar(requireActivity());
                        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, new AccountFragment())
                                        .commit();
                                progressBar.setVisibility(View.GONE);
                            }
                        }, 1000);
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ImageView ivProfile = view.findViewById(R.id.imageView_profile);
        TextView tvName = view.findViewById(R.id.userName);

        sharedPreferences = requireActivity().getSharedPreferences("login_state", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnProfileDetail = view.findViewById(R.id.profileInformation);
        btnEditProfile = view.findViewById(R.id.updateInfo);
        btnChangePassword = view.findViewById(R.id.changePassword);
        btnLogout = view.findViewById(R.id.logout);
        btnExit = view.findViewById(R.id.exitApp);

        user = mAuth.getCurrentUser();
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

        btnProfileDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUpdateProfile();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo.launch(new Intent(requireActivity(), UpdateAccountInfo.class));
            }
        });

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

    public void DialogUpdateProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile_information, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        ShapeableImageView ivUpload = view.findViewById(R.id.uploadAvatar);
        TextView tvDisplayName = view.findViewById(R.id.tvDisplayName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvBirthday = view.findViewById(R.id.tvBirthday);
        TextView tvBio = view.findViewById(R.id.tvBio);

        if (user.getPhotoUrl() != null) {
            String photoUrlString = Objects.requireNonNull(user.getPhotoUrl()).toString();
            Glide.with(this).load(photoUrlString).fitCenter().into(ivUpload);
            Log.i("photoUrl", photoUrlString);
        } else {
            Glide.with(requireActivity())
                    .load(avatarImagesRef.child(getUID()))
                    .fitCenter()
                    .into(ivUpload);
        }

        Log.i("imageUri", "Set done: " + avatarImagesRef.child(getUID()));


        tvDisplayName.setText(getDisplayName());

        tvEmail.setText(getUserEmail());

        getBirthdayAndBio(new Callback() {
            @Override
            public void onBirthdayLoaded(String birthday) {
                Log.i("birthday", birthday);
                tvBirthday.setText(birthday);
            }

            @Override
            public void onBioLoaded(String bio) {
                Log.i("bio", bio);
                tvBio.setText(bio);
            }
        });

        ImageButton btnClose = view.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}