package com.miwth.and102_asm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements ProductDAO {

    private final Context context;
    private final ArrayList<Product> productArrayList;

    public ProductAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.product_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        holder.productName.setText(productArrayList.get(position).getProductName());
        holder.productPrice.setText(String.valueOf(productArrayList.get(position).getProductPrice()));
        holder.productQuantity.setText(String.valueOf(productArrayList.get(position).getProductQuantity()));
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productQuantity = itemView.findViewById(R.id.tv_product_quantity);
        }
    }
}
