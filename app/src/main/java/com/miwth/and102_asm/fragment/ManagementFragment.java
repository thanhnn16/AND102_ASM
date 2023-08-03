package com.miwth.and102_asm.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.AddProductActivity;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.database.UploadCallBack;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;

public class ManagementFragment extends Fragment implements ProductDAO, UserAuth {

    Button btnAddProduct, btnAddCategory;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ActivityResultLauncher<Intent> getNewProduct = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.i("ProductManagement", "onActivityResult: result OK");
                    Intent intent = result.getData();
                    if (intent == null) {
                        Log.e("ProductManagement", "onActivityResult: intent is null");
                        return;
                    }
                    Product product = intent.getParcelableExtra("product");
                    ArrayList<Uri> uriListResult = intent.getParcelableArrayListExtra("uri_list");
                    if (product != null) {
                        Log.i("ProductManagement", "onActivityResult: got product");
                        insert(product, getUID());
                        if (uriListResult != null) {
                            Log.i("ProductManagement", "onActivityResult: got uri list");
                            UploadCallBack uploadCallBack = new UploadCallBack() {
                                @Override
                                public void onUploadComplete() {
                                    Log.i("ProductManagement", "onUploadComplete: ");
                                }

                                @Override
                                public void onUploadError(Throwable throwable) {
                                    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("ProductManagement", "onUploadError: ", throwable);
                                }
                            };
                            uploadProductImg(uriListResult, getUID(), product.getProductID(), requireContext(), uploadCallBack);
                            Log.i("ProductManagement", "onActivityResult: uploading");
                            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                            bottomNavigationView.setSelectedItemId(R.id.product_home);
                        }
                    }
                }
            });

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_management, container, false);

        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);

        mDatabase.child(getUID()).get().addOnCompleteListener(task -> {
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

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProductActivity.class);
            intent.putParcelableArrayListExtra("productArrayList", productArrayList);
            getNewProduct.launch(intent);
        });
        return view;
    }
}