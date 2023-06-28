package com.miwth.and102_asm.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miwth.and102_asm.model.Product;

import java.util.ArrayList;

public interface ProductDAO{
    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://and102-asm-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    default void insert(Product product, String userId) {
        mDatabase.child(userId).child(String.valueOf(product.getProductID())).setValue(product);
    }

    default ArrayList<Product> readFromFirebase (String userId) {
        DatabaseReference userRef = mDatabase.child(userId);
        ArrayList<Product> productArrayList = new ArrayList<>();
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    ArrayList<Product> products = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        products.add(product);
                    }
                    productArrayList.addAll(products);
                } else {
                    // Không có dữ liệu trong snapshot
                }
            } else {
                // Lỗi khi lấy dữ liệu
                Log.e("ProductManagement", "Error getting initial products", task.getException());
            }
        });
        return productArrayList;
    }

    default void update(Product product) {
        mDatabase.child(String.valueOf(product.getProductID())).setValue(product);
    }

    default void delete(Product product) {
        mDatabase.child(String.valueOf(product.getProductID())).removeValue();
    }



}