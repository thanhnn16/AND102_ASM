package com.miwth.and102_asm.database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ProductDetail extends AppCompatActivity implements ProductDAO, UserAuth {

    ViewPager2 productImageRv;
    TextView productName, productPrice, productQty, productDescription, productCategory;
    ImageButton btnBack;
    Product product;
    FirebaseUser user;
    LottieAnimationView loading;
    ArrayList<Uri> uriArrayList;
    ArrayList<Uri> tempImgList = new ArrayList<>();
    ProductDetailAdapter productDetailAdapter;
    Toolbar toolbar;

    ActivityResultLauncher<Intent> updateProduct = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Product updatedProduct = intent.getParcelableExtra("updatedProduct");
                        if (updatedProduct != null) {
                            product = updatedProduct;
                            tempImgList = intent.getParcelableArrayListExtra("updatedImgList");
                            uriArrayList.clear();
                            uriArrayList.addAll(tempImgList);
                            productDetailAdapter.notifyDataSetChanged();
                            getProduct();
                        }
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    Log.d("Update", "Update cancelled");
                } else {
                    Log.d("Update", "Update failed");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        init();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        productDetailAdapter = new ProductDetailAdapter(this, uriArrayList);

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
        toolbar = findViewById(R.id.toolbar);

        product = getIntent().getParcelableExtra("product");
        Log.d("Product", "Product: " + product.getProductID());
        getProduct();
        uriArrayList = getImagesListUri();
        getTempImgList();

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
        productDescription.setText(product.getProductDes());
        productCategory.setText(product.getProductCategoryName());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.detail_menu_edit) {
            Intent intent = new Intent(this, UpdateProductActivity.class);
            intent.setAction("update");
            intent.putExtra("product", product);
            intent.putExtra("tempImgList", tempImgList);
            updateProduct.launch(intent);
        } else if (id == R.id.detail_menu_delete) {
            deleteProduct();
            Log.d("Product", "Delete product");
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTempImgList() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(formatter);
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String randomName = formattedDateTime + "_" + randomNumber;

        StorageReference ref = productImagesRef.child(getUID()).child(String.valueOf(product.getProductID()));

        ref.listAll().addOnSuccessListener(listResult -> {
            int i = 0;
            for (StorageReference item : listResult.getItems()) {
                String fileName = "temp_" + randomName + i + ".jpg";
                File localFile = new File(getCacheDir(), fileName);
                item.getFile(localFile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tempImgList.add(Uri.fromFile(localFile));
                        Log.d("Product", "Temp img list: " + tempImgList.size());
                        Log.d("Product", "Temp img added: " + localFile.toString());
                    }
                });
                i++;
            }
        });
    }
}