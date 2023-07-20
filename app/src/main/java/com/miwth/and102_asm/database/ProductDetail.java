package com.miwth.and102_asm.database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.ProductDetailAdapter;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;

public class ProductDetail extends AppCompatActivity implements ProductDAO, UserAuth {

    ViewPager2 productImageRv;
    TextView productName, productPrice, productQty, productDescription, productCategory;
    ImageButton btnBack;
    Product product;
    FirebaseUser user;
    LottieAnimationView loading;
    ArrayList<Uri> uriArrayList;
    ProductDetailAdapter productDetailAdapter;

    ActivityResultLauncher<Intent> updateProduct = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Product updatedProduct = intent.getParcelableExtra("updatedProduct");
                            if (updatedProduct != null) {
                                delete(String.valueOf(product.getProductID()), user.getUid());
                                insert(updatedProduct, user.getUid());
                                product = updatedProduct;
                                showLoading();
                                new Handler().postDelayed(() -> getProduct(), 2000);
                            }
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Log.d("Update", "Update cancelled");
                    } else {
                        Log.d("Update", "Update failed");
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        init();
        // Get product from intent
        product = getIntent().getParcelableExtra("product");
        uriArrayList = getImagesListUri();
        getProduct();

        productDetailAdapter = new ProductDetailAdapter(this, uriArrayList);
        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setReverseLayout(false);
        productImageRv.setLayoutManager(linearLayoutManager);*/
        productImageRv.setAdapter(productDetailAdapter);
        productImageRv.setClipToPadding(false);
        productImageRv.setClipChildren(false);
        productImageRv.setOffscreenPageLimit(3);
        productImageRv.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        productImageRv.setPageTransformer(compositePageTransformer);

        productImageRv.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private int currentPosition = 0;
            private boolean isAtLastItem = false;
            private boolean isScrollingToStart = false;

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                // Check if the user is at the last item (uriArrayList.size() - 1)
                isAtLastItem = position == uriArrayList.size() - 1;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Check if the user is at the last item and scrolling towards the start
                if (isAtLastItem && positionOffsetPixels == 0 && isScrollingToStart) {
                    productImageRv.setCurrentItem(0, true);
                    isScrollingToStart = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Check if the user is starting to scroll towards the start
                if (state == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == uriArrayList.size() - 1) {
                    isScrollingToStart = true;
                }
            }
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void deleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setIcon(R.drawable.baseline_clear_48);
        builder.setMessage("Are you sure you want to delete this product?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            delete(String.valueOf(product.getProductID()), user.getUid());
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void init() {
        user = mAuth.getCurrentUser();
        productImageRv = findViewById(R.id.product_image_rv);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productQty = findViewById(R.id.product_quantity);
        productCategory = findViewById(R.id.product_category);
        productDescription = findViewById(R.id.product_description);
        btnBack = findViewById(R.id.tvGoBack);
        loading = findViewById(R.id.img_loading_animation);
    }

    @SuppressLint("SetTextI18n")
    private void getProduct() {

        if (product == null) {
            Log.d("Product", "Product is null");
            return;
        }
        Log.d("Product", "Got product");
        productName.setText(product.getProductName());
        productPrice.setText("Price: " + product.getProductPrice() + "$");
        productQty.setText("Qty: " + product.getProductQuantity());
//        productCategory.setText(product.getProductCategory());
        productDescription.setText(product.getProductDes());
    }

    private void showLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(LayoutInflater.from(this).inflate(
                R.layout.loading_after_update, null));
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        new Handler().postDelayed(dialog::dismiss, 3000);
    }

    @SuppressLint("NotifyDataSetChanged")
    public ArrayList<Uri> getImagesListUri() {
        ArrayList<Uri> uriList = new ArrayList<>();
        Task<ListResult> listResultTask = productImagesRef.child(user.getUid())
                .child(String.valueOf(product.getProductID())).listAll();
        listResultTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (StorageReference item : task.getResult().getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(uri -> {
                        uriList.add(uri);
                        if (uriList.size() == task.getResult().getItems().size()) {
                            productDetailAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(exception -> {
                        // Xử lý lỗi tại đây nếu cần thiết.
                        Log.e("getImagesListUri", "Error fetching image URL", exception);
                    });
                }
            } else {
                Log.e("getImagesListUri", "Error getting image list: " + task.getException());
            }
        });
        return uriList;
    }
}