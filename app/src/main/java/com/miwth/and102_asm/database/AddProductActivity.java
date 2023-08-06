package com.miwth.and102_asm.database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.ImageListAdapter;
import com.miwth.and102_asm.fragment.ProductHomeFragment;
import com.miwth.and102_asm.model.LoadingDialog;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity implements ProductDAO, UserAuth {

    EditText edtProductID, edtProductName, edtProductPrice, edtProductQuantity, edtProductCategory, edtProductDescription;
    TextInputLayout tilProductCategory;
    Button btnAddProduct;
    ImageButton iBtnBack;
    ProductHomeFragment productHomeFragment;
    ImageView imgProduct;
    TextView tvUploadImage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<Uri> imageList = new ArrayList<>();
    ImageListAdapter imageListAdapter;
    RecyclerView imgList_rv;
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
        setContentView(R.layout.activity_add_product);
        LoadingDialog loadingDialog = new LoadingDialog(AddProductActivity.this);

        edtProductID = findViewById(R.id.etProductId);
        edtProductName = findViewById(R.id.etProductName);
        edtProductPrice = findViewById(R.id.etProductPrice);
        edtProductQuantity = findViewById(R.id.etProductQuantity);
        imgProduct = findViewById(R.id.ivProductImg);
        tvUploadImage = findViewById(R.id.tvUploadImage);
        edtProductCategory = findViewById(R.id.etProductCategory);
        edtProductDescription = findViewById(R.id.etProductDes);

        tilProductCategory = findViewById(R.id.tilProductCategory);
        tilProductCategory.setOnClickListener(v -> showChooseCategoryDialog());
        edtProductCategory.setOnClickListener(v -> showChooseCategoryDialog());

        imageListAdapter = new ImageListAdapter(this, imageList);
        imgList_rv = findViewById(R.id.imgList_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imgList_rv.setLayoutManager(linearLayoutManager);
        imgList_rv.setAdapter(imageListAdapter);


        iBtnBack = findViewById(R.id.btn_back);
        iBtnBack.setOnClickListener(v -> finish());

        Intent getData = getIntent();
        if (getData != null) {
            productArrayList = getIntent().getParcelableArrayListExtra("productArrayList");
        } else {
            Log.d("AddProductActivity", "Intent is null");
        }

        productHomeFragment = new ProductHomeFragment();
        btnAddProduct = findViewById(R.id.btnAddProduct);

        imgProduct.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            mGetMultiImage.launch(intent);
        });

        btnAddProduct.setOnClickListener(v -> {
            String productID = edtProductID.getText().toString();
            String productName = edtProductName.getText().toString();
            String productPrice = edtProductPrice.getText().toString();
            String productQuantity = edtProductQuantity.getText().toString();
            String productDescription = edtProductDescription.getText().toString();

            if (productID.isEmpty()) {
                edtProductID.setError("Please enter product ID");
                edtProductID.requestFocus();
                return;
            }
            if (productArrayList.size() > 0) {
                for (int i = 0; i < productArrayList.size(); i++) {
                    if (productArrayList.get(i).getProductID() == Integer.parseInt(productID)) {
                        edtProductID.setError("Product ID already exists");
                        edtProductID.requestFocus();
                        return;
                    }
                }
            }

            if (productName.isEmpty()) {
                edtProductName.setError("Please enter product name");
                edtProductName.requestFocus();
                return;
            }
            if (productPrice.isEmpty()) {
                edtProductPrice.setError("Please enter product price");
                edtProductPrice.requestFocus();
                return;
            }
            if (productQuantity.isEmpty()) {
                edtProductQuantity.setError("Please enter product quantity");
                edtProductQuantity.requestFocus();
                return;
            }

            Product product =
                    new Product(Integer.parseInt(productID), productName,
                            Float.parseFloat(productPrice), Integer.parseInt(productQuantity),
                            selectedCategory, productDescription);
            Intent intent = new Intent();
            if (imageList == null) {
                Log.e("Error", "Image URI is null");
                Toast.makeText(this, "Choose an image", Toast.LENGTH_SHORT).show();
            } else {
                UploadCallBack uploadCallBack = new UploadCallBack() {
                    @Override
                    public void onUploadComplete() {
                        Log.i("ProductManagement", "onUploadComplete: ");
                        Toast.makeText(AddProductActivity.this, "Upload complete", Toast.LENGTH_SHORT).show();
                        loadingDialog.cancel();
                        finish();
                    }

                    @Override
                    public void onUploadError(Throwable throwable) {
                        Toast.makeText(AddProductActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.cancel();
                        Log.e("ProductManagement", "onUploadError: ", throwable);
                    }
                };
                intent.putExtra("product", product);
                intent.putExtra("uri_list", imageList);
                loadingDialog.show();
                insert(product, getUID());
                uploadProductImg(imageList, getUID(), product.getProductID(), this, uploadCallBack);
                Log.i("ProductManagement", "onActivityResult: uploading");
            }
        });
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
            edtProductCategory.setText(category);
        });
        builder.create().show();
    }
}