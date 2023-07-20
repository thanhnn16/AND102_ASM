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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.ProductAdapter;
import com.miwth.and102_asm.database.AddProductActivity;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.database.UploadCallBack;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;

public class ProductManagementFragment extends Fragment implements ProductDAO, UserAuth {
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    ArrayList<Product> productArrayList;
    LottieAnimationView lottieAnimationView;
    SwipeRefreshLayout swipeRefreshLayout;
    ExtendedFloatingActionButton fabAdd;
    ImageSlider imageSlider;
    ProgressBar progressBar;
//    SearchView searchView;

    ActivityResultLauncher<Intent> getNewProduct = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) {
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
                                        productArrayList.add(product);
                                        productAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onUploadError(Throwable throwable) {
                                        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("ProductManagement", "onUploadError: ", throwable);
                                    }
                                };
                                uploadProductImg(uriListResult, getUID(), product.getProductID(), requireContext(), uploadCallBack);
                                Log.i("ProductManagement", "onActivityResult: uploading");
                            }
                        }
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
        progressBar = view.findViewById(R.id.progressBarProductManagement);
        imageSlider = view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel("https://cf.shopee.vn/file/vn-50009109-fa79715264f5c973648d8096a8aa9773_xxhdpi"
                , ScaleTypes.FIT));
        imageList.add(new SlideModel("https://cf.shopee.vn/file/vn-50009109-6ab8507af131b31c7b27e74a8dfc3247_xxhdpi"
                , ScaleTypes.FIT));
        imageList.add(new SlideModel("https://cf.shopee.vn/file/vn-50009109-04e4988439e96c5566fbf9974770d142_xxhdpi"
                , ScaleTypes.FIT));
        imageList.add(new SlideModel("https://cf.shopee.vn/file/vn-50009109-6444782a4c08a461885c8cdc417b6900_xxhdpi"
                , ScaleTypes.FIT));

        imageSlider.setImageList(imageList, ScaleTypes.FIT);

        int spanCount = 2; // Number of columns in the grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter(getContext(), productArrayList);
        recyclerView.setAdapter(productAdapter);
//        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddProduct);
        fabAdd = view.findViewById(R.id.fabAddProduct);
        lottieAnimationView = view.findViewById(R.id.animationViewLoading);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutProductManagement);
//        searchView = view.findViewById(R.id.search_view);
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Product> filtered = new ArrayList<>();

                if (newText == null || newText.length() == 0) {
                    filtered.addAll(productArrayList);
                } else {
                    for (Product product : productArrayList) {
                        if (product.getProductName().toLowerCase().contains(newText)
                                || product.getProductDes().toLowerCase().contains(newText)) {
                            filtered.add(product);
                        }
                    }
                }
                productAdapter.filterList(filtered);
                return true;
            }
        });*/

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

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProductManagementFragment())
                    .commit();
            swipeRefreshLayout.setRefreshing(false);
            productAdapter.notifyDataSetChanged();
        }, 600));
        return view;
    }

    public void stopAnimation() {
        lottieAnimationView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        lottieAnimationView.cancelAnimation();
    }
}