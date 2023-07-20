package com.miwth.and102_asm.users;

import static com.yalantis.ucrop.UCrop.RESULT_ERROR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.miwth.and102_asm.MainActivity;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ImageCropperActivity;

public class ChangeUserPictureActivity extends AppCompatActivity implements UserAuth {
    Button btnUpload;
    ShapeableImageView ivProfilePicture;
    FirebaseUser user;
    Uri photoUrl;
    ActivityResultLauncher<Intent> getCroppedImage = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    Uri resultUri = Uri.parse(intent.getStringExtra("resultUri"));
                    photoUrl = resultUri;
                    ivProfilePicture.setImageURI(resultUri);
                }
            } else if (result.getResultCode() == RESULT_ERROR) {
                Toast.makeText(ChangeUserPictureActivity.this, "Image cropping error", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChangeUserPictureActivity.this, "Image cropping cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    });
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new
            ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            Intent intent = new Intent(ChangeUserPictureActivity.this, ImageCropperActivity.class);
            intent.putExtra("stockImgUrl", result.toString());
            getCroppedImage.launch(intent);
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_picture);

        btnUpload = findViewById(R.id.btnUpload);
        ivProfilePicture = findViewById(R.id.ivProductImg);
        user = mAuth.getCurrentUser();

        btnUpload.setOnClickListener(v -> uploadImage(photoUrl));
        ivProfilePicture.setOnClickListener(v -> chooseImage());

    }

    private void uploadImage(Uri imageUri) {
        uploadAvatar(imageUri, user);
        showSuccessDialog();
    }

    private void chooseImage() {
        mGetContent.launch("image/*");
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_signup_successfully, null);
        builder.setView(view);
        Button btnStart = view.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(ChangeUserPictureActivity.this, MainActivity.class));
            finishAffinity();
        });
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        AlertDialog dialog = builder.create();
        builder.show();
    }
}