package com.miwth.and102_asm.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.users.UpdateAccountInfo;
import com.miwth.and102_asm.users.UserAuth;
import com.miwth.and102_asm.welcome.LoginSignupScreen;

public class AccountFragment extends Fragment implements UserAuth, ProductDAO {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RelativeLayout btnProfileDetail, btnEditProfile, btnChangePassword, btnLogout, btnExit;
    FirebaseUser user;

    ActivityResultLauncher<Intent> updateInfo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    showLoadingDialog();
                    new Handler().postDelayed(() -> requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new AccountFragment())
                            .commit(), 1400);
                }
            }
    );

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        ImageView ivProfile = view.findViewById(R.id.imageView_profile);
        TextView tvName = view.findViewById(R.id.userName);
        TextView tvEmail = view.findViewById(R.id.userEmail);

        sharedPreferences = requireActivity().getSharedPreferences("login_state", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnProfileDetail = view.findViewById(R.id.profileInformation);
        btnEditProfile = view.findViewById(R.id.updateInfo);
        btnChangePassword = view.findViewById(R.id.changePassword);
        btnLogout = view.findViewById(R.id.logout);
        btnExit = view.findViewById(R.id.exitApp);

        user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() == null) {
                tvName.setText("Hello User");
            } else {
                tvName.setText(user.getDisplayName());
                tvEmail.setText(user.getEmail());
            }
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).fitCenter().into(ivProfile);
                Log.i("photoUrl", user.getPhotoUrl().toString());
            } else if (avatarImagesRef.child(getUID()).getPath().isEmpty()) {
                Log.i("photoUrl", "No photo");
                Glide.with(requireActivity())
                        .load(avatarImagesRef.child(getUID()))
                        .into(ivProfile);
            } else {
                Glide.with(requireActivity())
                        .load(R.drawable.account)
                        .into(ivProfile);
            }
        }

        btnProfileDetail.setOnClickListener(v -> DialogUpdateProfile());

        btnEditProfile.setOnClickListener(v -> updateInfo.launch(new Intent(requireActivity(), UpdateAccountInfo.class)));

        btnLogout.setOnClickListener(v -> {
//                build new confirm dialog to confirm log out
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Logout");
            builder.setIcon(R.drawable.log_out);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                logout();
                startActivity(new Intent(requireActivity(), LoginSignupScreen.class));
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_state", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                requireActivity().finishAffinity();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        btnExit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Exit");
            builder.setIcon(R.drawable.exit_app);
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> requireActivity().finishAffinity());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
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


        if (user.getDisplayName() == null) {
            tvDisplayName.setText("Hello User");
        } else {
            tvDisplayName.setText(user.getDisplayName());
        }
        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).fitCenter().into(ivUpload);
            Log.i("photoUrl", user.getPhotoUrl().toString());
        } else if (avatarImagesRef.child(getUID()).getPath().isEmpty()) {
            Log.i("photoUrl", "No photo");
            Glide.with(requireActivity())
                    .load(avatarImagesRef.child(getUID()))
                    .fitCenter()
                    .into(ivUpload);
        } else {
            Glide.with(requireActivity())
                    .load(R.drawable.baseline_account_circle_24)
                    .fitCenter()
                    .into(ivUpload);
        }

        Log.i("imageUri", "Set done: " + avatarImagesRef.child(getUID()));

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

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_after_update, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(dialog::dismiss, 1500);
    }
}