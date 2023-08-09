package com.miwth.and102_asm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.database.ProductDAO;
import com.miwth.and102_asm.database.ProductDetail;
import com.miwth.and102_asm.model.Product;
import com.miwth.and102_asm.users.UserAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements ProductDAO, UserAuth, Filterable {

    private final Context context;
    private final ArrayList<Product> productArrayList;
    private ArrayList<Product> filteredList;

    public ProductAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
        filteredList = productArrayList;
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
        Product product = filteredList.get(holder.getBindingAdapterPosition());
        holder.productName.setText(product.getProductName());
        String productID = String.valueOf(product.getProductID());
        String imgURI = getDefaultImgUri(getUID(), productID);

        Log.i("ProductAdapter", "onBindViewHolder: " + imgURI);
        String fileName = "temp_" + productID + product.getProductName() + product.getProductPrice() + ".jpg";
        File localFile = new File(context.getCacheDir(), fileName);

        if (localFile.exists()) {
            // If the local file already exists, load the image directly from the local file
            Glide.with(holder.iv_product_image.getContext())
                    .load(localFile)
                    .into(holder.iv_product_image);
            holder.loading.cancelAnimation();
            holder.loading.setVisibility(View.GONE);
            holder.iv_product_image.setVisibility(View.VISIBLE);
        } else {
            // If the local file does not exist, fetch it from Firebase Storage
            getDefaultProductImagesRef(getUID(), productID)
                    .getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        // Local temp file has been created
                        Glide.with(holder.iv_product_image.getContext())
                                .load(localFile)
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
        Intent intent = new Intent(context, ProductDetail.class).setAction(Intent.ACTION_VIEW);
        intent.putExtra("product", product);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Product> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(productArrayList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Product product : productArrayList) {
                        if (product.getProductName().toLowerCase().contains(filterPattern)) {
                            filtered.add(product);
                        }
                    }
                }
                results.values = filtered;
                results.count = filtered.size();
                return results;
            }


            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Product> products) {
        filteredList = new ArrayList<>();
        filteredList.addAll(products);
        Log.i("ProductAdapter", "filterList: " + filteredList.size());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
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
