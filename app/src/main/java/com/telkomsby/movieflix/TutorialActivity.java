package com.telkomsby.movieflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager2 tutorialViewPager;
    private LinearLayout layoutOnboardingIndicators;
    private Button buttonNext;
    private TextView textSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialViewPager = findViewById(R.id.tutorialViewPager);
        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonNext = findViewById(R.id.buttonNext);
        textSkip = findViewById(R.id.textSkip);

        final List<TutorialItem> items = new ArrayList<>();
        items.add(new TutorialItem("Title 1", "Description 1", R.drawable.ic_launcher_foreground));
        items.add(new TutorialItem("Title 2", "Description 2", R.drawable.ic_launcher_foreground));
        items.add(new TutorialItem("Title 3", "Description 3", R.drawable.ic_launcher_foreground));

        tutorialViewPager.setAdapter(new TutorialAdapter(items));
        setupIndicators(items.size());
        setCurrentIndicator(0);

        tutorialViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
                if (position == items.size() - 1) {
                    buttonNext.setText("Get Started");
                    textSkip.setVisibility(View.GONE);
                } else {
                    buttonNext.setText("Next");
                    textSkip.setVisibility(View.VISIBLE);
                }
            }
        });


        textSkip.setOnClickListener(v -> {
            goToLogin();
        });

        buttonNext.setOnClickListener(v -> {
            if (tutorialViewPager.getCurrentItem() + 1 < items.size()) {
                tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem() + 1);
            } else {
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    i == index ? R.drawable.indicator_active : R.drawable.indicator_inactive
            ));
        }
    }
}


