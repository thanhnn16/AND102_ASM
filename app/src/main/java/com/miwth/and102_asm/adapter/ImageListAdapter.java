package com.miwth.and102_asm.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.miwth.and102_asm.R;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    Context context;
    ArrayList<Uri> uriArrayList;

    public ImageListAdapter(Context context, ArrayList<Uri> uriArrayList) {
        this.context = context;
        this.uriArrayList = uriArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.image_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.thumbnail_image.setImageURI(uriArrayList.get(position));
        Glide.with(context).load(uriArrayList
                        .get(position)).centerCrop()
                .override(300, 400)
                .into(holder.thumbnail_image);
        holder.thumbnail_image.setOnClickListener(v -> {
            ShapeableImageView product_img = ((Activity) context).findViewById(R.id.ivProductImg);
            product_img.setImageURI(uriArrayList.get(holder.getAbsoluteAdapterPosition()));
        });
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView thumbnail_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail_image = itemView.findViewById(R.id.thumbnail_image);
        }
    }
}
