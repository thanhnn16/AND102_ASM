package com.miwth.and102_asm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miwth.and102_asm.R;
import com.miwth.and102_asm.model.ProductCategory;

import java.util.ArrayList;

public class CategoriesHomeFragmentAdapter extends RecyclerView.Adapter<CategoriesHomeFragmentAdapter.ViewHolder> {
    private final ChangeCategoryCallback changeCategoryCallback;
    Context context;
    ArrayList<ProductCategory> categoryArrayList;

    public CategoriesHomeFragmentAdapter(Context context, ArrayList<ProductCategory> categoryArrayList, ChangeCategoryCallback changeCategoryCallback) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.changeCategoryCallback = changeCategoryCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.category_home_list_item, parent, false);
        return new CategoriesHomeFragmentAdapter.ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCategory category = categoryArrayList.get(position);

        holder.categoryImage.setImageResource(categoryArrayList.get(position).getCategoryImage());

        boolean isCategorySelected = categoryArrayList.get(position).isSelected();
        if (isCategorySelected) {
            holder.categoryLayout.setBackgroundColor(context.getResources().getColor(R.color.send_disable, context.getTheme()));
            holder.categoryImage.setColorFilter(context.getResources().getColor(R.color.white, context.getTheme())); // White Tint
        } else {
            holder.categoryLayout.setBackgroundColor(context.getResources().getColor(R.color.white, context.getTheme()));
            holder.categoryImage.setColorFilter(context.getResources().getColor(R.color.send_enable, context.getTheme())); // Black Tint
        }

        holder.categoryLayout.setOnClickListener(v -> {
            changeCategoryCallback.changeCategory(category.getCategoryID());
            Log.d("TAG", "onClick: " + category.getCategoryID());
            for (ProductCategory c : categoryArrayList) {
                c.setSelected(false);
            }
            category.setSelected(true);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public void setSelectionByCategoryID(int categoryID) {
        for (ProductCategory c : categoryArrayList) {
            c.setSelected(false);
        }
        categoryArrayList.get(categoryID).setSelected(true);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        RelativeLayout categoryLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryLayout = itemView.findViewById(R.id.layout);
        }
    }
}
