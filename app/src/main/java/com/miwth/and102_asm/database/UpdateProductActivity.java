package com.miwth.and102_asm.database;

import static com.yalantis.ucrop.UCrop.RESULT_ERROR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class UpdateProductActivity extends AppCompatActivity implements ProductDAO, UserAuth {
    ImageButton btn_back;
    ShapeableImageView imgProduct;
    Button btnUpdate;
    TextInputEditText etID, etName, etPrice, etQuantity;
    Uri stockImgUrl;
    ArrayList<Product> productArrayList;
    Product currentProduct;

    ActivityResultLauncher<Intent> getCroppedImage = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    Log.i("imgUri", "Got stock uri path: " + intent.getStringExtra("resultUri"));
                    Uri resultUri = Uri.parse(intent.getStringExtra("resultUri"));
                    stockImgUrl = resultUri;
                    Log.i("imgUri", "Got new stock uri path: " + stockImgUrl.getPath() + " Got uri string: " + stockImgUrl.toString());
                    imgProduct.setImageURI(resultUri);
                }
            } else if (result.getResultCode() == RESULT_ERROR) {
                Toast.makeText(UpdateProductActivity.this, "Image cropping error", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UpdateProductActivity.this, "Image cropping cancelled", Toast.LENGTH_SHORT).show();
            }

        }
    });

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new
            ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            Intent intent = new Intent(UpdateProductActivity.this, ImageCropperActivity.class);
            intent.putExtra("stockImgUrl", result.toString());
            getCroppedImage.launch(intent);
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    });


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

        Intent intent = getIntent();
        currentProduct = intent.getParcelableExtra("product");
        if (currentProduct == null) {
            Log.e("Error", "Product is null");
            Toast.makeText(this, "Product is null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            etID.setText(String.valueOf(currentProduct.getProductID()));
            etName.setText(currentProduct.getProductName());
            etPrice.setText(currentProduct.getProductPrice());
            etQuantity.setText(String.valueOf(currentProduct.getProductQuantity()));
            String imgURI = currentProduct.getImgSrc();
            stockImgUrl = Uri.parse(imgURI);
            String productID = String.valueOf(currentProduct.getProductID());
            if (imgURI != null) {
                File localFile;
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    Log.e("Error", Objects.requireNonNull(e.getMessage()));
                    return;
                }
                File finalLocalFile = localFile;
                productImagesRef.child(productID).getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    // Local temp file has been created
                    Glide.with(this)
                            .load(finalLocalFile)
                            .into(imgProduct);
                    Log.i("Success", "Success");
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.e("Error", Objects.requireNonNull(exception.getMessage()));
                });
            }
        }
        btn_back.setOnClickListener(v -> finish());
        imgProduct.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnUpdate.setOnClickListener(v -> setBtnUpdate());
    }

    private void setBtnUpdate() {

        String productID = Objects.requireNonNull(etID.getText()).toString();
        String productName = Objects.requireNonNull(etName.getText()).toString();
        String productPrice = Objects.requireNonNull(etPrice.getText()).toString();
        String productQuantity = Objects.requireNonNull(etQuantity.getText()).toString();
        String productImg = "gs://and102-asm.appspot.com/avatar/" + productID;

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

        Product product = new Product(Integer.parseInt(productID), productName,
                productPrice, Integer.parseInt(productQuantity), productImg, Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        Intent intent = new Intent();
        if (stockImgUrl == null) {
            Log.e("Error", "Image URI is null");
            Toast.makeText(this, "Choose an image", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("updatedProduct", product);
            intent.putExtra("imgUri", stockImgUrl.toString());
            Log.i("imgUri", "Got uri path: " + stockImgUrl.getPath() + " Got uri string: " + stockImgUrl.toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}