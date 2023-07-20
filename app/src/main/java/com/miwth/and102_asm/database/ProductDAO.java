package com.miwth.and102_asm.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

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


public interface ProductDAO {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://and102-asm-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://and102-asm.appspot.com");
    StorageReference productImagesRef = storage.getReference().child("product_img");
    StorageReference avatarImagesRef = storage.getReference().child("avatar_img");

    default void insert(Product product, String userId) {
        mDatabase.child(userId).child(String.valueOf(product.getProductID())).setValue(product);
    }

    default void uploadProductImg(Uri imgUri, int productID, Context context) {
//        productImagesRef.child(String.valueOf(productID)).putFile(imgUri);

//        compress image before upload to firebase
        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(imgUri);
            Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream);
            if (bitmapImage == null) {
                Log.e("Error", "Failed to decode image file: " + imgUri);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            bitmapImage.recycle();

            File outputFile = new File(context.getCacheDir(), "temp");
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
            productImagesRef.child(String.valueOf(productID)).putFile(Uri.fromFile(outputFile));
        } catch (FileNotFoundException e) {
            Log.e("Error", "File not found: " + e.getMessage());
        }

    }


    default void update(Product product, String uid) {
        mDatabase.child(uid).child(String.valueOf(product.getProductID())).setValue(product);
    }

    default void delete(Product product, String uid) {
        mDatabase.child(uid).child(String.valueOf(product.getProductID())).removeValue();
        productImagesRef.child(String.valueOf(product.getProductID())).delete();
    }
}