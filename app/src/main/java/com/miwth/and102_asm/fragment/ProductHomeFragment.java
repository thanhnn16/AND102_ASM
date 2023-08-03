package com.miwth.and102_asm.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.CategoriesHomeFragmentAdapter;
import com.miwth.and102_asm.adapter.ChangeCategoryCallback;
import com.miwth.and102_asm.adapter.ProductAdapter;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.model.ProductCategory;
import com.miwth.and102_asm.users.UserAuth;

import java.util.ArrayList;
import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public class ProductHomeFragment extends Fragment implements ProductDAO, UserAuth, ChangeCategoryCallback {
    RecyclerView recyclerView, recyclerViewCategories;
    ProductAdapter productAdapter;
    ArrayList<Product> productArrayList;
    ArrayList<ProductCategory> categoryArrayList;
    LottieAnimationView lottieAnimationView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvHello, tvNoProductFound, tvAllProducts;
    ImageButton voice_search;
    SearchView searchView;
    CategoriesHomeFragmentAdapter categoriesHomeFragmentAdapter;
    LinearLayout llCategories;
    ActivityResultLauncher<Intent> recognizeSpeech = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            List<String> results = data.getStringArrayListExtra(
                                    RecognizerIntent.EXTRA_RESULTS);
                            if (results != null) {
                                String spokenText = results.get(0);
                                Log.i("spokenText", "Spoken text: " + spokenText);
                                searchView.setQuery(spokenText, true);

                            }
                        }
                    }
                }
            });

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        productArrayList = new ArrayList<>();
        mDatabase.child(getUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                productArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                    stopAnimation();
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Log.e("ProductManagement", "Error getting data", task.getException());
            }
        });

        View view = inflater.inflate(R.layout.fragment_product_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewProductManagement);
        tvHello = view.findViewById(R.id.tvHello);
        tvHello.setText(getString(R.string.hello) + getDisplayName());
        tvNoProductFound = view.findViewById(R.id.tvNoProductFound);

        tvAllProducts = view.findViewById(R.id.tvAllProducts);

        int spanCount = 2;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter(getContext(), productArrayList);
        recyclerView.setAdapter(productAdapter);
        lottieAnimationView = view.findViewById(R.id.animationViewLoading);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutProductManagement);

        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);

        categoryArrayList = new ArrayList<>();
        categoryArrayList.add(new ProductCategory(99, "All", R.drawable.category_all));
        categoryArrayList.add(new ProductCategory(1, "Vegetables", R.drawable.category_vegetables));
        categoryArrayList.add(new ProductCategory(2, "Fruits", R.drawable.category_fruits));
        categoryArrayList.add(new ProductCategory(3, "Dairy", R.drawable.category_dairy));
        categoryArrayList.add(new ProductCategory(4, "Bread", R.drawable.category_bread));
        categoryArrayList.add(new ProductCategory(5, "Eggs", R.drawable.category_eggs));
        categoryArrayList.add(new ProductCategory(6, "Mushroom", R.drawable.category_mushroom));
        categoryArrayList.add(new ProductCategory(7, "Oats", R.drawable.category_oats));
        categoryArrayList.add(new ProductCategory(8, "Rice", R.drawable.category_rice));

        categoriesHomeFragmentAdapter = new CategoriesHomeFragmentAdapter(getContext(), categoryArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategories.setLayoutManager(linearLayoutManager);
        recyclerViewCategories.setAdapter(categoriesHomeFragmentAdapter);

        voice_search = view.findViewById(R.id.voice_search);
        voice_search.setOnClickListener(v -> displaySpeechRecognizer());
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return true;
            }
        });

        ValueEventListener productsValueEventListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
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

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProductHomeFragment())
                    .commit();
            swipeRefreshLayout.setRefreshing(false);
            productAdapter.notifyDataSetChanged();
        }, 600));

        llCategories = view.findViewById(R.id.llShowCategories);

        llCategories.setOnClickListener(v -> {
            SmoothBottomBar bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
//            bottomNavigationView.setSelectedItemId(R.id.category_menu);
            bottomNavigationView.setItemActiveIndex(1);
            bottomNavigationView.setSelected(true);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CategoryFragment())
                    .commit();
        });

        tvAllProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAdapter.filterList(productArrayList);
            }
        });


        return view;
    }

    public void stopAnimation() {
        lottieAnimationView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        lottieAnimationView.cancelAnimation();
        tvNoProductFound.setVisibility(View.GONE);
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizeSpeech.launch(intent);
    }

    private ArrayList<Product> filterByCategory(int categoryID) {
        ArrayList<Product> filteredList = new ArrayList<>();
        for (Product product : productArrayList) {
            if (product.getProductCategoryId() == categoryID) {
                Log.d("categoryID", "filterByCategory: " + product.getProductCategoryId());
                filteredList.add(product);
            }
        }
        return filteredList;
    }

    @Override
    public void changeCategory(int categoryID) {
        Log.i("categoryID", "changeCategory position: " + categoryID);
        if (categoryID == 99) {
            productAdapter.filterList(productArrayList);
        } else {
            ArrayList<Product> filteredList = filterByCategory(categoryID);
            if (filteredList.size() == 0) {
                tvNoProductFound.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvNoProductFound.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                productAdapter.filterList(filteredList);
            }
        }

    }
}
