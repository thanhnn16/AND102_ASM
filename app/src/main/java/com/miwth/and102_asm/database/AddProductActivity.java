package com.miwth.and102_asm.database;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.fragment.ProductManagementFragment;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity implements ProductDAO, UserAuth {

    EditText edtProductID, edtProductName, edtProductPrice, edtProductQuantity;
    Button btnAddProduct;
    ProductManagementFragment productManagementFragment;
    ImageView imgProduct;
    TextView tvUploadImage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Uri stockImgUrl;
    ArrayList<Product> productArrayList = new ArrayList<>();

    ActivityResultLauncher<Intent> getCroppedImage = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    Uri resultUri = Uri.parse(intent.getStringExtra("resultUri"));
                    stockImgUrl = resultUri;
                    imgProduct.setImageURI(resultUri);
                    tvUploadImage.setVisibility(View.GONE);
                }
            }
        }
    });

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new
            ActivityResultContracts.GetContent(), result -> {
        Intent intent = new Intent(AddProductActivity.this, ImageCropperActivity.class);
        intent.putExtra("stockImgUrl", result.toString());
        getCroppedImage.launch(intent);
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        edtProductID = findViewById(R.id.etProductId);
        edtProductName = findViewById(R.id.etProductName);
        edtProductPrice = findViewById(R.id.etProductPrice);
        edtProductQuantity = findViewById(R.id.etProductQuantity);
        imgProduct = findViewById(R.id.imgProduct);
        tvUploadImage = findViewById(R.id.tvUploadImage);

        Intent getData = getIntent();
        if (getData != null) {
            productArrayList = getIntent().getParcelableArrayListExtra("productArrayList");
        } else {
            Log.d("AddProductActivity", "Intent is null");
        }

        productManagementFragment = new ProductManagementFragment();
        btnAddProduct = findViewById(R.id.btnAddProduct);

        imgProduct.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnAddProduct.setOnClickListener(v -> {
            String productID = edtProductID.getText().toString();
            String productName = edtProductName.getText().toString();
            String productPrice = edtProductPrice.getText().toString();
            String productQuantity = edtProductQuantity.getText().toString();
            String productImg = "gs://and102-asm.appspot.com/avatar/" + productID;

            if (productID.isEmpty()) {
                edtProductID.setError("Please enter product ID");
                edtProductID.requestFocus();
                return;
            }
            if (productArrayList.size() > 0) {
                for (int i = 0; true; i++) {
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

            Product product = new Product(Integer.parseInt(productID), productName,
                    productPrice, Integer.parseInt(productQuantity), productImg, Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            Intent intent = new Intent();
            if (stockImgUrl == null) {
                Log.e("Error", "Image URI is null");
                Toast.makeText(this, "Choose an image", Toast.LENGTH_SHORT).show();
            } else {
                intent.putExtra("product", product);
                intent.putExtra("imgUri", stockImgUrl.toString());
                Log.i("imgUri", "Got uri path: " + stockImgUrl.getPath() + " Got uri string: " + stockImgUrl.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}