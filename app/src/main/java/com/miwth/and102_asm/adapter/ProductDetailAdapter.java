package com.miwth.and102_asm.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.miwth.and102_asm.R;

import java.util.ArrayList;

public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.ViewHolder> {
    Context context;
    ArrayList<Uri> imageList;

    public ProductDetailAdapter(Context context, ArrayList<Uri> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_detail_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LottieAnimationView img_loading_animation = ((Activity) context).findViewById(R.id.img_loading_animation);
        Glide.with(context).load(imageList.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("glide", "can't load image");
                        img_loading_animation.setVisibility(View.VISIBLE);
                        img_loading_animation.playAnimation();
                        holder.product_image.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.i("glide", "loaded image");
                        holder.product_image.setImageDrawable(resource);
                        img_loading_animation.cancelAnimation();
                        img_loading_animation.setVisibility(View.GONE);
                        holder.product_image.setVisibility(View.VISIBLE);
                        return true;
                    }
                })
                .centerCrop()
                .into(holder.product_image);

        holder.tv_img_count.setText(position + 1 + "/" + getItemCount());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView product_image;
        TextView tv_img_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            tv_img_count = itemView.findViewById(R.id.tv_img_count);
        }
    }
}
