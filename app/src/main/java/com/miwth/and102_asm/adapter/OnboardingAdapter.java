package com.miwth.and102_asm.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miwth.and102_asm.R;
import com.miwth.and102_asm.model.OnboardingModel;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final Context context;
    private final List<OnboardingModel> onboardingModels;

    public OnboardingAdapter(Context context, List<OnboardingModel> onboardingModels) {
        this.context = context;
        this.onboardingModels = onboardingModels;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((Activity) context)
                .inflate(R.layout.onboarding_items_container, parent, false);


        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {

        holder.title.setText(onboardingModels.get(position).getTitle());
        holder.desc.setText(onboardingModels.get(position).getDesc());
        holder.imageView.setImageResource(onboardingModels.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return onboardingModels.size();
    }

    public static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private TextView title, desc;
        private ImageView imageView;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.onboardindTitle);
            desc = itemView.findViewById(R.id.onboardingDesc);
            imageView = itemView.findViewById(R.id.onboardingImg);
        }
    }
}
