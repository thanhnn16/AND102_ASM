package com.miwth.and102_asm.users;

import static com.yalantis.ucrop.UCrop.RESULT_ERROR;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ImageCropperActivity;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class UpdateAccountInfo extends AppCompatActivity implements UserAuth {

    ImageButton btnBack;
    TextInputEditText etDisplayName, etDOB, etBio;
    Button btnUpdate, btnBack2;
    Uri photoUrl;
    ShapeableImageView ivProfilePicture;
    TextView tvUploadImage;
    Toolbar toolbar;
    FirebaseUser user = mAuth.getCurrentUser();
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
                    tvUploadImage.setVisibility(View.GONE);
                }
            }
            if (result.getResultCode() == RESULT_ERROR) {
                onBackPressed();
                Toast.makeText(UpdateAccountInfo.this, "Image cropping cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    });

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new
            ActivityResultContracts.GetContent(), result -> {
        Intent intent = new Intent(UpdateAccountInfo.this, ImageCropperActivity.class);
        intent.putExtra("stockImgUrl", result.toString());
        getCroppedImage.launch(intent);
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account_info);

        getWindow().setStatusBarColor(getResources().getColor(R.color.btn_login_disabled, getTheme()));

        tvUploadImage = findViewById(R.id.tvUploadImage);
        toolbar = findViewById(R.id.toolbar);
        photoUrl = user.getPhotoUrl();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
        }

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnBack = findViewById(R.id.btn_back);
        etDisplayName = findViewById(R.id.etDisplayName);
        etDOB = findViewById(R.id.etDOB);
        etBio = findViewById(R.id.etBio);

        Glide.with(this).load(user.getPhotoUrl()).fitCenter().into(ivProfilePicture);
        etDisplayName.setText(user.getDisplayName());
        getBirthdayAndBio(new Callback() {
            @Override
            public void onBirthdayLoaded(String birthday) {
                Log.i("birthday", birthday);
                etDOB.setText(birthday);
            }

            @Override
            public void onBioLoaded(String bio) {
                Log.i("bio", bio);
                etBio.setText(bio);
            }
        });

        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack2 = findViewById(R.id.btnGoBack);


        btnBack.setOnClickListener(v -> onBackPressed());
        btnBack2.setOnClickListener(v -> onBackPressed());

        ivProfilePicture.setOnClickListener(v -> mGetContent.launch("image/*"));

        etDOB.setOnFocusChangeListener((v, hasFocus) -> {
            if (etDOB.hasFocus()) {
                showDatePickerDialog();
            }
        });
        etDOB.setOnClickListener(v -> showDatePickerDialog());

        btnUpdate.setOnClickListener(v -> {
            String displayName = String.valueOf(etDisplayName.getText());
            String dob = String.valueOf(etDOB.getText());
            String bio = String.valueOf(etBio.getText());

            if (displayName.isEmpty()) {
                etDisplayName.setError("Display name is required");
                etDisplayName.requestFocus();
                return;
            }
            if (photoUrl == null) {
                Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            }
            updateProfile(Objects.requireNonNull(mAuth.getCurrentUser()), displayName, dob, bio, photoUrl, this);
            setResult(RESULT_OK);
            finish();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth1) -> {
            // Xử lý ngày được chọn
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth1, monthOfYear + 1, year1);
            etDOB.setText(selectedDate);
        }, dayOfMonth, month, year);
        Calendar minDate = Calendar.getInstance();
        minDate.set(1980, Calendar.JANUARY, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2015, Calendar.DECEMBER, 31);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }
}