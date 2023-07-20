package com.miwth.and102_asm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.database.ProductDetail;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements ProductDAO, UserAuth {

    private final Context context;
    private final ArrayList<Product> productArrayList;
    private ArrayList<Product> filteredList;
    public ProductAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
        filteredList = new ArrayList<>(productArrayList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.product_item_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        Product product = productArrayList.get(holder.getAdapterPosition());
        holder.productName.setText(product.getProductName());
        String imgURI = productArrayList.get(holder.getAdapterPosition()).getImgSrc();
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
            productImagesRef.child(Objects.requireNonNull(mAuth.getCurrentUser())
                            .getUid()).child(productID).child("default")
                    .getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        // Local temp file has been created
                        Glide.with(holder.iv_product_image.getContext())
                                .load(finalLocalFile)
                                .into(holder.iv_product_image);
                        holder.loading.cancelAnimation();
                        holder.loading.setVisibility(View.GONE);
                        holder.iv_product_image.setVisibility(View.VISIBLE);
                        Log.i("Success", "Success");
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                        Log.e("Error", Objects.requireNonNull(exception.getMessage()));
                    });
        }

        holder.productItemLayout.setOnClickListener(v -> {
            // Open product detail activity
            openProductDetailActivity(product);
        });
    }

    private void openProductDetailActivity(Product product) {
        Intent intent = new Intent(context, ProductDetail.class);
        intent.putExtra("product", product);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Product> filtered) {
        productArrayList.clear();
        productArrayList.addAll(filtered);
        notifyDataSetChanged();
    }

    public void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_after_update, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(dialog::dismiss, 500);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView iv_product_image;
        LinearLayout productItemLayout;
        LottieAnimationView loading;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
            productItemLayout = itemView.findViewById(R.id.cl_product_item_list);
            loading = itemView.findViewById(R.id.img_loading_animation);
        }
    }
}
