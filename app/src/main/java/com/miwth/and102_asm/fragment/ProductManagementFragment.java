package com.miwth.and102_asm.fragment;

import static android.app.Activity.RESULT_OK;

import static com.miwth.and102_asm.users.UserAuth.mAuth;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.ProductAdapter;
import com.miwth.and102_asm.database.AddProductActivity;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementFragment extends Fragment implements ProductDAO, UserAuth {

    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<Product> productArrayList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public ProductManagementFragment() {
    }

    ActivityResultLauncher<Intent> getNewProduct = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            Toast.makeText(getActivity(), "Intent is null", Toast.LENGTH_SHORT).show();;
                            return;
                        }
                        Product product = (Product) intent.getSerializableExtra("product");
                        productArrayList.add(product);
                        productAdapter.notifyDataSetChanged();
                        Log.i("ProductManagement", "onActivityResult: got product");
                        insert(product, getUID());
                    }
                }
            });

    public static ProductManagementFragment newInstance(String param1, String param2) {
        ProductManagementFragment fragment = new ProductManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        productArrayList = readFromFirebase(getUID());
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_product_management, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewProductManagement);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        productAdapter = new ProductAdapter(getContext(), (ArrayList<Product>) productArrayList);
        recyclerView.setAdapter(productAdapter);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddProduct);

        ValueEventListener productsValueEventListener = new ValueEventListener() {
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

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewProduct.launch(new Intent(getContext(), AddProductActivity.class));
            }
        });

        return view;
    }





}