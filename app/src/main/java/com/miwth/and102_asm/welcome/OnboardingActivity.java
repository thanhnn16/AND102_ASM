package com.miwth.and102_asm.welcome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.miwth.and102_asm.R;
import com.miwth.and102_asm.adapter.OnboardingAdapter;
import com.miwth.and102_asm.model.OnboardingModel;
import com.miwth.and102_asm.users.SignUpActivity;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    final Context context = this;
    Button btnNext;
    ImageView btnBack;
    ViewPager2 viewPager2;
    LinearLayout layoutOnboardingIndicators;
    List<OnboardingModel> onboardingModelList;
    OnboardingAdapter onboardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager2 = findViewById(R.id.viewPager2);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        layoutOnboardingIndicators = findViewById(R.id.onboardingIndicators);

        onboardingModelList = new ArrayList<>();
        onboardingModelList.add(
                new OnboardingModel(R.drawable.onboarding_1
                        , "Welcome to Fresh Fruits"
                        , "Fast and responsibily delivery by our courier service" +
                        "consectetur adipiscing elit, sed do " +
                        "ex ea commodo consequat. Duis aute irure dolor " +
                        "in reprehenderit in voluptate velit esse cillum " +
                        "dolore eu fugiat nulla pariatur. Excepteur sint"));

        onboardingModelList.add(
                new OnboardingModel(R.drawable.onboarding_2
                        , "We provide best quality Fruits to your family"
                        , "Fast and responsibily delivery by our courier service" +
                        "consectetur adipiscing elit, sed do " +
                        "ex ea commodo consequat. Duis aute irure dolor " +
                        "in reprehenderit in voluptate velit esse cillum " +
                        "dolore eu fugiat nulla pariatur. Excepteur sint"));


        onboardingModelList.add(
                new OnboardingModel(R.drawable.onboarding_3
                        , "Fast and responsibily delivery by our courier service"
                        , "Lorem ipsum dolor sit amet, " +
                        "consectetur adipiscing elit, sed do " +
                        "ex ea commodo consequat. Duis aute irure dolor " +
                        "in reprehenderit in voluptate velit esse cillum " +
                        "dolore eu fugiat nulla pariatur. Excepteur sint"));

        onboardingAdapter = new OnboardingAdapter(context, onboardingModelList);
        viewPager2.setAdapter(onboardingAdapter);
        setUpOnboardingIndicators();
        setCurrentIndicator(0);
        btnBack.setOnClickListener(v -> onBackPressed());

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager2.getCurrentItem();
                if (currentItem + 1 < onboardingAdapter.getItemCount()) {
                    viewPager2.setCurrentItem(currentItem + 1);
                } else {
                    startActivity(new Intent(OnboardingActivity.this, SignUpActivity.class));
                }
            }
        });
    }

    private void setUpOnboardingIndicators() {

        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        Log.i("ChildCount", "Childcount: " + childCount);
        for (int i = 0; i < childCount; i++) {
            Log.i("ChildCount", "i: " + i);
            Log.i("ChildCount", "Index : " + index);
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if (index == 0) {
            btnBack.setVisibility(View.INVISIBLE);
        } else {
            btnBack.setVisibility(View.VISIBLE);
        }
        if (index == onboardingAdapter.getItemCount() - 1) {
            btnNext.setText("Start");
        } else {
            btnNext.setText("Next");
        }
    }
}