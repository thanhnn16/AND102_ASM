package com.miwth.and102_asm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private ArrayList<Product> filteredList;
    public ProductAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
        filteredList = new ArrayList<>();
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
        Product product = productArrayList.get(position);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getProductPrice() + "$");
        holder.productQuantity.setText("Qty: " + product.getProductQuantity());
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
            productImagesRef.child(productID).getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Local temp file has been created
                Glide.with(holder.iv_product_image.getContext())
                        .load(finalLocalFile)
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
            showLoadingDialog();
            productArrayList.remove(holder.getAdapterPosition());
        });

        holder.btnSeeMore.setOnClickListener(v -> showMoreDiaglog(position));
    }

    @SuppressLint("SetTextI18n")
    private void showMoreDiaglog(int position) {
        Product product = productArrayList.get(position);
        String productID = String.valueOf(productArrayList.get(position).getProductID());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_detail_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        ImageView productImg = view.findViewById(R.id.productImg);
        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvProductPrice = view.findViewById(R.id.tvProductPrice);
        TextView tvProductQty = view.findViewById(R.id.tvProductQty);
        TextView tvProductDescription = view.findViewById(R.id.tvProductDescription);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        tvProductName.setText(product.getProductName());
        tvProductPrice.setText(product.getProductPrice() + "$");
        tvProductQty.setText("Qty: " + product.getProductQuantity());
//        tvProductDescription.setText(product.getProductDescription());
        String imgURI = productArrayList.get(position).getImgSrc();
        if (imgURI != null) {
            File localFile;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                return;
            }
            File finalLocalFile = localFile;
            productImagesRef.child(productID).getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Local temp file has been created
                Glide.with(productImg.getContext())
                        .load(finalLocalFile)
                        .into(productImg);
                Log.i("Success", "Success");
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e("Error", Objects.requireNonNull(exception.getMessage()));
            });
        }
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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
        TextView productName, productPrice, productQuantity;
        ImageButton btnEdit, btnDelete, btnSeeMore;
        ImageView iv_product_image;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productQuantity = itemView.findViewById(R.id.tv_product_quantity);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
            btnSeeMore = itemView.findViewById(R.id.btn_see_more);
        }
    }
}
