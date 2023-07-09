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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.fragment.ProductManagementFragment;
import com.miwth.and102_asm.model.Product;

import java.util.Objects;

public class AddProductActivity extends AppCompatActivity{

    EditText edtProductID, edtProductName, edtProductPrice, edtProductQuantity;
    Button btnAddProduct;
    ProductManagementFragment productManagementFragment;
    ImageView imgProduct;
    TextView tvUploadImage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Uri imgUri;

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                imgUri = data.getData();
                imgProduct.setImageURI(imgUri);
                tvUploadImage.setVisibility(View.GONE);
                }
        }
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

        productManagementFragment = new ProductManagementFragment();
        btnAddProduct = findViewById(R.id.btnAddProduct);

        imgProduct.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            mGetContent.launch(Intent.createChooser(intent, "Select Picture"));
        });
        btnAddProduct.setOnClickListener(v -> {
            String productID = edtProductID.getText().toString();
            String productName = edtProductName.getText().toString();
            String productPrice = edtProductPrice.getText().toString();
            String productQuantity = edtProductQuantity.getText().toString();
            String productImg = "gs://and102-asm.appspot.com/avatar/" + productID;

            if (productID.isEmpty() || productName.isEmpty() || productPrice.isEmpty() || productQuantity.isEmpty()) {
                Toast.makeText(AddProductActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            Product product = new Product(Integer.parseInt(productID), productName,
                    productPrice, Integer.parseInt(productQuantity), productImg, Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            Intent intent = new Intent();
            if (imgUri == null) {
                Log.e("Error", "Image URI is null");
            } else {
                intent.putExtra("product", product);
                intent.putExtra("imgUri", imgUri.toString());
                Log.i("imgUri", "Got uri path: " + imgUri.getPath() + " Got uri string: " + imgUri.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}