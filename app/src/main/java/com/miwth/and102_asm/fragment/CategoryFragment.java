package com.miwth.and102_asm.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.CategoryAdapter;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.model.ProductCategory;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements ProductDAO, UserAuth {
    SwipeRefreshLayout swipeRefreshLayout;
    CategoryAdapter categoryAdapter;
    ArrayList<ProductCategory> categoryArrayList;
    RecyclerView recyclerView;

    @SuppressLint({"SetJavaScriptEnabled", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryArrayList = new ArrayList<>();
        categoryArrayList.add(new ProductCategory(1, "Vegetables", R.drawable.category_vegetables));
        categoryArrayList.add(new ProductCategory(2, "Fruits", R.drawable.category_fruits));
        categoryArrayList.add(new ProductCategory(3, "Dairy", R.drawable.category_dairy));
        categoryArrayList.add(new ProductCategory(4, "Bread", R.drawable.category_bread));
        categoryArrayList.add(new ProductCategory(5, "Eggs", R.drawable.category_eggs));
        categoryArrayList.add(new ProductCategory(6, "Mushroom", R.drawable.category_mushroom));
        categoryArrayList.add(new ProductCategory(7, "Oats", R.drawable.category_oats));
        categoryArrayList.add(new ProductCategory(8, "Rice", R.drawable.category_rice));
        getItemQuantity();
        categoryAdapter = new CategoryAdapter(requireActivity(), categoryArrayList);
        productCatalogRef.setValue(categoryArrayList);
        recyclerView = view.findViewById(R.id.rv_category);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(categoryAdapter);

        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            categoryAdapter.notifyDataSetChanged();
        }, 1000));

        return view;
    }

    public void getItemQuantity() {
        mDatabase.child(getUID()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Product product = categorySnapshot.getValue(Product.class);
                    assert product != null;
                    int productCategoryID = product.getProductCategoryId();
                    switch (productCategoryID) {
                        case 1:
                            int vegetablesQuantity = categoryArrayList.get(0).getCategoryQuantity();
                            categoryArrayList.get(0).setCategoryQuantity(vegetablesQuantity + product.getProductQuantity());
                            break;

                        case 2:
                            int fruitsQuantity = categoryArrayList.get(1).getCategoryQuantity();
                            categoryArrayList.get(1).setCategoryQuantity(fruitsQuantity + product.getProductQuantity());
                            break;

                        case 3:
                            int dairyQuantity = categoryArrayList.get(2).getCategoryQuantity();
                            categoryArrayList.get(2).setCategoryQuantity(dairyQuantity + product.getProductQuantity());
                            break;

                        case 4:
                            int breadQuantity = categoryArrayList.get(3).getCategoryQuantity();
                            categoryArrayList.get(3).setCategoryQuantity(breadQuantity + product.getProductQuantity());
                            break;

                        case 5:
                            int eggsQuantity = categoryArrayList.get(4).getCategoryQuantity();
                            categoryArrayList.get(4).setCategoryQuantity(eggsQuantity + product.getProductQuantity());
                            break;

                        case 6:
                            int mushroomQuantity = categoryArrayList.get(5).getCategoryQuantity();
                            categoryArrayList.get(5).setCategoryQuantity(mushroomQuantity + product.getProductQuantity());
                            break;

                        case 7:
                            int oatsQuantity = categoryArrayList.get(6).getCategoryQuantity();
                            categoryArrayList.get(6).setCategoryQuantity(oatsQuantity + product.getProductQuantity());
                            break;

                        case 8:
                            int riceQuantity = categoryArrayList.get(7).getCategoryQuantity();
                            categoryArrayList.get(7).setCategoryQuantity(riceQuantity + product.getProductQuantity());
                            break;
                        default:
                            break;
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Database Error", error.getMessage());
            }
        });
    }
}