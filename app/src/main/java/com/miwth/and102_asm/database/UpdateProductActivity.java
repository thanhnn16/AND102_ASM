package com.miwth.and102_asm.database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.ImageListAdapter;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;
import java.util.Objects;

public class UpdateProductActivity extends AppCompatActivity implements ProductDAO, UserAuth {
    ImageButton btn_back;
    ShapeableImageView imgProduct;
    Button btnUpdate;
    TextInputEditText etID, etName, etPrice, etQuantity, etCategory, etDes;
    TextView tvUploadImage;
    Uri stockImgUrl;
    ArrayList<Product> productArrayList;
    Product currentProduct;
    ArrayList<Uri> imageList;
    ImageListAdapter imageListAdapter;
    RecyclerView imgList_rv;
    FirebaseUser user;
    int selectedCategory = 0;

    ActivityResultLauncher<Intent> mGetMultiImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageList.clear();
                            if (data.getClipData() != null) {
                                Log.d("image", "got images");
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    imageList.add(imageUri);
                                }
                            } else if (data.getData() != null) {
                                Log.d("image", "got 1 image");
                                Uri imageUri = data.getData();
                                imageList.add(imageUri);
                            }
                        } else {
                            Log.d("image", "no image");
                        }
                        imgProduct.setImageURI(imageList.get(0));
                        tvUploadImage.setVisibility(View.GONE);
                        imageListAdapter.notifyDataSetChanged();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        btn_back = findViewById(R.id.btn_back);
        imgProduct = findViewById(R.id.ivProductImg);
        btnUpdate = findViewById(R.id.btnUpdate);

        etID = findViewById(R.id.etProductId);
        etName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etProductPrice);
        etQuantity = findViewById(R.id.etProductQuantity);
        etCategory = findViewById(R.id.etProductCategory);
        etDes = findViewById(R.id.etProductDes);


        tvUploadImage = findViewById(R.id.tvUploadImage);
        imgList_rv = findViewById(R.id.imgList_rv);

        user = mAuth.getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            currentProduct = intent.getParcelableExtra("product");
            imageList = intent.getParcelableArrayListExtra("tempImgList");
            if (imageList == null) {
                imageList = new ArrayList<>();
            }
            imgProduct.setImageURI(imageList.get(0));
        }
        if (currentProduct == null) {
            Log.e("Error", "Product is null");
            Toast.makeText(this, "Product is null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            etID.setText(String.valueOf(currentProduct.getProductID()));
            etName.setText(currentProduct.getProductName());
            etPrice.setText(String.valueOf(currentProduct.getProductPrice()));
            etQuantity.setText(String.valueOf(currentProduct.getProductQuantity()));
            etCategory.setText(currentProduct.getProductCategoryName());
            etDes.setText(currentProduct.getProductDes());
        }
        btn_back.setOnClickListener(v -> finish());
        imgProduct.setOnClickListener(v -> mGetMultiImage.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)));

        imageListAdapter = new ImageListAdapter(this, imageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imgList_rv.setLayoutManager(linearLayoutManager);
        imgList_rv.setAdapter(imageListAdapter);

        productArrayList = new ArrayList<>();
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
        etCategory.setOnClickListener(v -> showChooseCategoryDialog());
        btnUpdate.setOnClickListener(v -> setBtnUpdate());
    }

    private void setBtnUpdate() {
        String productID = Objects.requireNonNull(etID.getText()).toString();
        String productName = Objects.requireNonNull(etName.getText()).toString();
        String productPrice = Objects.requireNonNull(etPrice.getText()).toString();
        String productQuantity = Objects.requireNonNull(etQuantity.getText()).toString();
        String productDescription = Objects.requireNonNull(etDes.getText()).toString();

        if (productID.isEmpty()) {
            etID.setError("Please enter product ID");
            etID.requestFocus();
            return;
        }
        if (Integer.parseInt(productID) != currentProduct.getProductID()) {
            if (productArrayList.size() > 0) {
                for (int i = 0; i < productArrayList.size(); i++) {
                    if (productArrayList.get(i).getProductID() == Integer.parseInt(productID)) {
                        etID.setError("Product ID already exists");
                        etID.requestFocus();
                        return;
                    }
                }
            }
        }

        if (productName.isEmpty()) {
            etName.setError("Please enter product name");
            etName.requestFocus();
            return;
        }
        if (productPrice.isEmpty()) {
            etPrice.setError("Please enter product price");
            etPrice.requestFocus();
            return;
        }
        if (productQuantity.isEmpty()) {
            etQuantity.setError("Please enter product quantity");
            etQuantity.requestFocus();
            return;
        }
        if (imageList == null) {
            Log.e("Error", "Image URI is null");
            Toast.makeText(this, "Choose an image", Toast.LENGTH_SHORT).show();
        } else {
            Product product = new Product(
                    Integer.parseInt(productID), productName, Float.parseFloat(productPrice), Integer.parseInt(productQuantity), selectedCategory, productDescription
            );
            UploadCallBack uploadCallBack = new UploadCallBack() {
                @Override
                public void onUploadComplete() {
                    Log.i("ProductManagement", "onUploadComplete: ");
                    Toast.makeText(UpdateProductActivity.this, "Update complete", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("updatedProduct", product);
                    intent.putExtra("updatedImgList", imageList);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onUploadError(Throwable throwable) {
                    Toast.makeText(UpdateProductActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProductManagement", "onUploadError: ", throwable);
                }
            };
            delete(String.valueOf(currentProduct.getProductID()), getUID());
            insert(product, getUID());
            uploadProductImg(imageList, getUID(), product.getProductID(), this, uploadCallBack);
        }
    }

    private void showChooseCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose category");
        String[] categories = {"Vegetables", "Fruits", "Dairy", "Bread", "Eggs", "Mushroom", "Oats", "Rice", "Others"};
        builder.setItems(categories, (dialog, which) -> {
            String category = categories[which];
            selectedCategory = which + 1;
            Log.d("AddProductActivity", "selected category name: " + category);
            Log.d("AddProductActivity", "selected category: " + selectedCategory);
            etCategory.setText(category);
        });
        builder.create().show();
    }
}