package com.miwth.and102_asm.database;

import static com.yalantis.ucrop.UCrop.RESULT_ERROR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.miwth.and102_asm.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class ImageCropperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper2);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String imgUri = intent.getStringExtra("stockImgUrl");
            if (imgUri != null) {
                String destFileName = "IMG_" +
                        System.currentTimeMillis() +
                        ".jpg";
                Uri destUri = createImageUri(Uri.parse(imgUri), destFileName);
                UCrop.of(Uri.parse(imgUri), destUri)
                        .withOptions(new UCrop.Options())
                        .withAspectRatio(3, 4)
                        .withMaxResultSize(500, 500)
                        .start(ImageCropperActivity.this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                Uri resultUri = UCrop.getOutput(data);
                Intent intent = new Intent();
                intent.putExtra("resultUri", String.valueOf(resultUri));
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (resultCode == RESULT_ERROR) {
            if (data != null) {
                setResult(RESULT_ERROR);
                finish();
                Throwable error = UCrop.getError(data);
                error.printStackTrace();
            }
        }
    }

    private Uri createImageUri(Uri sourceUri, String fileName) {
        File cacheDir = getExternalCacheDir();
        File destFile = new File(cacheDir, fileName);

        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            OutputStream outputStream = Files.newOutputStream(destFile.toPath());
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(destFile);


  /*      ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        values.put(MediaStore.Images.Media.DATA, destFile.getAbsolutePath());

        ContentResolver resolver = getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        return resolver.insert(collection, values);*/
    }
}