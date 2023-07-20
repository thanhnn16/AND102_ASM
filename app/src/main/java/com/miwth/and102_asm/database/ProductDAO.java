package com.miwth.and102_asm.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miwth.and102_asm.MainActivity;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.model.Product;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public interface ProductDAO {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://and102-asm-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://and102-asm.appspot.com");
    StorageReference productImagesRef = storage.getReference().child("product_img");
    StorageReference avatarImagesRef = storage.getReference().child("avatar_img");

    default void insert(Product product, String userId) {
        mDatabase.child(userId).child(String.valueOf(product.getProductID())).setValue(product);
    }

    default void uploadProductImg(ArrayList<Uri> imgUriList, String uID, int productID, Context context, UploadCallBack callBack) {
        int totalImages = imgUriList.size();
        ProgressBar progressBar = ((Activity) context).findViewById(R.id.progressBarProductManagement);
        MainActivity mainActivity = new MainActivity();
        progressBar.setVisibility(View.VISIBLE);
        final CountDownLatch latch = new CountDownLatch(totalImages);

        for (int i = 0; i < totalImages; i++) {
            Uri imgUri = imgUriList.get(i);
            Log.i("imgUri", imgUri.toString());
            InputStream inputStream;
            try {
                inputStream = context.getContentResolver().openInputStream(imgUri);
                Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream);
                if (bitmapImage == null) {
                    Log.e("Error", "Failed to decode image file: " + imgUri);
                } else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    bitmapImage.recycle();
                    File outputFile = new File(context.getCacheDir(), "temp" + i + ".jpg");
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(outputFile);
                        fos.write(baos.toByteArray());
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        Log.e("Error", "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.e("Error", "Error accessing file: " + e.getMessage());
                    }
                    if (i == 0) {
                        productImagesRef.child(uID)
                                .child(String.valueOf(productID))
                                .child("default")
                                .putFile(Uri.fromFile(outputFile)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        latch.countDown();

                                    }
                                });
                    } else {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
                        String fileName = productID + now.format(formatter) + i;
                        Log.i("fileName", fileName);
                        productImagesRef.child(uID)
                                .child(String.valueOf(productID))
                                .child(fileName).putFile(Uri.fromFile(outputFile)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        latch.countDown();
                                    }
                                });
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Wait until all images are uploaded (latch count reaches 0) or timeout occurs
                                latch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // All images have been uploaded, hide the progress bar and call the callback
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    callBack.onUploadComplete();
                                }
                            });
                        }
                    }).start();
                }
            } catch (FileNotFoundException e) {
                Log.e("Error", "File not found: " + e.getMessage());
            }
        }
    }

    default void delete(String id, String uid) {
        mDatabase.child(uid).child(id).removeValue();
        StorageReference ref = productImagesRef.child(uid).child(id);
        ref.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.delete();
            }
        });
    }
}