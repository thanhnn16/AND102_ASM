package com.miwth.and102_asm.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    DatabaseReference productCatalogRef = mDatabase.child("product_catalog");
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://and102-asm.appspot.com");
    StorageReference productImagesRef = storage.getReference().child("product_img");
    StorageReference avatarImagesRef = storage.getReference().child("avatar_img");


    default void insert(Product product, String userId) {
        mDatabase.child(userId).child(String.valueOf(product.getProductID())).setValue(product);
    }

    default void uploadProductImg(ArrayList<Uri> imgUriList, String uID, int productID, Context context, UploadCallBack callBack) {
        int totalImages = imgUriList.size();
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
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);
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
                                .putFile(Uri.fromFile(outputFile)).addOnCompleteListener(task -> latch.countDown());
                    } else {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
                        String fileName = productID + now.format(formatter) + i;
                        Log.i("fileName", fileName);
                        productImagesRef.child(uID)
                                .child(String.valueOf(productID))
                                .child(fileName).putFile(Uri.fromFile(outputFile)).addOnCompleteListener(task -> latch.countDown());
                    }
                    new Thread(() -> {
                        try {
                            // Wait until all images are uploaded (latch count reaches 0) or timeout occurs
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // All images have been uploaded, hide the progress bar and call the callback
                        ((Activity) context).runOnUiThread(callBack::onUploadComplete);
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

    default String getDefaultImgUri(String uid, String id) {
        return "gs://and102-asm.appspot.com/product_img/" + uid + "/" + id + "/default";
    }

    default StorageReference getDefaultProductImagesRef(String uid, String id) {
        return productImagesRef.child(uid).child(id).child("default");
    }

    default ArrayList<Product> getDataFromFirebase(String uid) {
        ArrayList<Product> productArrayList = new ArrayList<>();
        mDatabase.child((uid)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                productArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                }
            } else {
                Log.e("ProductManagement", "Error getting data", task.getException());
            }
        });
        return productArrayList;
    }
}