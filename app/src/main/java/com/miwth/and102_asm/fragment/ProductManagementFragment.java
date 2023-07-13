package com.miwth.and102_asm.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.ProductAdapter;
import com.miwth.and102_asm.database.AddProductActivity;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;

public class ProductManagementFragment extends Fragment implements ProductDAO, UserAuth {
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    ArrayList<Product> productArrayList;
    LottieAnimationView lottieAnimationView;
    SwipeRefreshLayout swipeRefreshLayout;

    ActivityResultLauncher<Intent> getNewProduct = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            Toast.makeText(getActivity(), "Intent is null", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Product product = intent.getParcelableExtra("product");
                        String imageUri = intent.getStringExtra("imgUri");
                        productArrayList.add(product);
                        productAdapter.notifyDataSetChanged();
                        Log.i("ProductManagement", "onActivityResult: got product");
                        assert product != null;
                        insert(product, getUID());
                        uploadImg(Uri.parse((imageUri)), product.getProductID(),
                                requireActivity());
                        stopAnimation();
                        swipeRefreshLayout.setRefreshing(true);
                        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 500);
                    }
                }
            });

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        productArrayList = new ArrayList<>();
        mDatabase.child(getUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                productArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                    stopAnimation();
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Log.e("ProductManagement", "Error getting data", task.getException());
            }
        });

        View view = inflater.inflate(R.layout.fragment_product_management, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewProductManagement);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        productAdapter = new ProductAdapter(getContext(), productArrayList);
        recyclerView.setAdapter(productAdapter);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddProduct);

        lottieAnimationView = view.findViewById(R.id.animationViewLoading);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutProductManagement);

        ValueEventListener productsValueEventListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductManagement", "onCancelled: ", error.toException());
            }
        };
        mDatabase.child(getUID()).addValueEventListener(productsValueEventListener);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProductActivity.class);
            intent.putParcelableArrayListExtra("productArrayList", productArrayList);
            getNewProduct.launch(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            productAdapter.notifyDataSetChanged();
        });
        return view;
    }

    public void stopAnimation() {
        lottieAnimationView.setVisibility(View.GONE);
        lottieAnimationView.cancelAnimation();
    }
}