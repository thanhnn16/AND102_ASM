package com.miwth.and102_asm.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.CategoryAdapter;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.model.ProductCategory;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements ProductDAO {
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
}