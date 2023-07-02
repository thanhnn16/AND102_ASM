package com.miwth.and102_asm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements ProductDAO, UserAuth {

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
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        holder.productName.setText(productArrayList.get(position).getProductName());
        holder.productPrice.setText(String.valueOf(productArrayList.get(position).getProductPrice()));
        holder.productQuantity.setText(String.valueOf(productArrayList.get(position).getProductQuantity()));
        String imgURI = productArrayList.get(position).getImgSrc();
        String productID = String.valueOf(productArrayList.get(position).getProductID());
        if (imgURI != null) {
            File localFile;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                return;
            }
            File finalLocalFile = localFile;
            imagesRef.child(productID).getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Local temp file has been created
                Glide.with(holder.iv_product_image.getContext())
                        .load(finalLocalFile)
                        .centerCrop()
                        .override(50, 50)
                        .into(holder.iv_product_image);
                Log.i("Success", "Success");
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e("Error", Objects.requireNonNull(exception.getMessage()));
            });
        }

        holder.btnEdit.setOnClickListener(v -> Toast.makeText(v.getContext(), "Edit", Toast.LENGTH_SHORT).show());

        holder.btnDelete.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Deleting " + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            delete(productArrayList.get(holder.getAdapterPosition()), getUID());
            productArrayList.remove(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageButton btnEdit, btnDelete;
        ImageView iv_product_image;
        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productQuantity = itemView.findViewById(R.id.tv_product_quantity);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
        }
    }
}
