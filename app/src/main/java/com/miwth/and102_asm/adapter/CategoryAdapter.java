package com.miwth.and102_asm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.miwth.and102_asm.R;
import com.miwth.and102_asm.model.ProductCategory;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductCategory> categoryArrayList;

    public CategoryAdapter(FragmentActivity fragmentActivity, ArrayList<ProductCategory> categoryArrayList) {
        this.context = fragmentActivity;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.category_list_item, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryImage.setImageResource(categoryArrayList.get(position).getCategoryImage());
        holder.categoryName.setText(categoryArrayList.get(position).getCategoryName());
        String quantity = categoryArrayList.get(position).getCategoryQuantity() + " items";
        holder.categoryQuantity.setText(quantity);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        TextView categoryQuantity;
        RelativeLayout categoryLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryQuantity = itemView.findViewById(R.id.category_quantity);
            categoryLayout = itemView.findViewById(R.id.cl_category_item_list);
        }
    }
}
