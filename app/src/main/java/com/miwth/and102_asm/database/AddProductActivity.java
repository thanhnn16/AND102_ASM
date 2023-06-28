package com.miwth.and102_asm.database;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.fragment.ProductManagementFragment;
import com.miwth.and102_asm.model.Product;

public class AddProductActivity extends AppCompatActivity{

    EditText edtProductID, edtProductName, edtProductPrice, edtProductQuantity, edtImgSrc;
    Button btnAddProduct;
    ProductManagementFragment productManagementFragment;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        edtProductID = findViewById(R.id.etProductId);
        edtProductName = findViewById(R.id.etProductName);
        edtProductPrice = findViewById(R.id.etProductPrice);
        edtProductQuantity = findViewById(R.id.etProductQuantity);
        edtImgSrc = findViewById(R.id.etProductImage);

        productManagementFragment = new ProductManagementFragment();

        btnAddProduct = findViewById(R.id.btnAddProduct);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productID = edtProductID.getText().toString();
                String productName = edtProductName.getText().toString();
                String productPrice = edtProductPrice.getText().toString();
                String productQuantity = edtProductQuantity.getText().toString();
                String productImgSrc = edtImgSrc.getText().toString();

                if (productID.isEmpty() || productName.isEmpty() || productPrice.isEmpty() || productQuantity.isEmpty() || productImgSrc.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }



                Product product = new Product(Integer.valueOf(productID), productName, productPrice, Integer.valueOf(productQuantity), productImgSrc, mAuth.getCurrentUser().getUid());

                Intent intent = new Intent();
                intent.putExtra("product", product);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}