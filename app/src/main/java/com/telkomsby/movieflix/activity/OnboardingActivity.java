package com.telkomsby.movieflix.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telkomsby.movieflix.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MovieFlixPrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean(KEY_FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
            setupTutorialUI();
        } else {
            checkUserSession();
        }
    }

    private void checkUserSession() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("TutorialActivity", "User session found. Checking subscription...");
            checkSubscriptionStatus(currentUser);
        } else {
            Log.d("TutorialActivity", "No user session. Navigating to LoginActivity.");
            navigateTo(LoginActivity.class);
        }
    }

    private void checkSubscriptionStatus(FirebaseUser user) {
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role)) {
                            navigateTo(AdminActivity.class);
                            return;
                        }

                        Boolean isSubscribed = documentSnapshot.getBoolean("isSubscribed");
                        Date expiryDate = documentSnapshot.getDate("subscriptionExpiry");
                        boolean hasActiveSubscription = isSubscribed != null && isSubscribed && expiryDate != null && expiryDate.after(new Date());

                        if (hasActiveSubscription) {
                            navigateTo(MainActivity.class);
                        } else {
                            navigateTo(PaymentActivity.class);
                        }
                    } else {
                        Toast.makeText(this, "Data tidak lengkap, silakan login kembali.", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        navigateTo(LoginActivity.class);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal verifikasi. Menuju halaman utama.", Toast.LENGTH_SHORT).show();
                    navigateTo(MainActivity.class);
                });
    }

    private void setupTutorialUI() {
        setContentView(R.layout.activity_onboarding);

        ViewPager2 tutorialViewPager = findViewById(R.id.tutorialViewPager);
        LinearLayout layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        MaterialButton buttonAction = findViewById(R.id.buttonAction);
        TextView textSkip = findViewById(R.id.textSkip);
        TextView textTitle = findViewById(R.id.textTitle);
        TextView textDescription = findViewById(R.id.textDescription);

        List<TutorialItem> items = new ArrayList<>();
        items.add(new TutorialItem("Selamat Datang di MovieFlix", "Temukan ribuan film dan serial favoritmu, semua dalam satu aplikasi.", R.drawable.ic_illustration_1));
        items.add(new TutorialItem("Film & Serial Terbaru", "Selalu update dengan tayangan terbaru dari berbagai genre, lokal dan internasional.", R.drawable.ic_illustration_2));
        items.add(new TutorialItem("Personalisasi Tayanganmu", "Rekomendasi khusus berdasarkan selera menontonmu. Tinggal klik “Mulai Sekarang” buat mulai eksplorasi!", R.drawable.ic_illustration_3));

        TutorialAdapter tutorialAdapter = new TutorialAdapter(items);
        tutorialViewPager.setAdapter(tutorialAdapter);

        ImageView[] indicators = new ImageView[items.size()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutOnboardingIndicators.addView(indicators[i]);
        }

        tutorialViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < layoutOnboardingIndicators.getChildCount(); i++) {
                    ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            i == position ? R.drawable.indicator_active : R.drawable.indicator_inactive));
                }
                textTitle.setText(items.get(position).getTitle());
                textDescription.setText(items.get(position).getDescription());
                MaterialButton buttonAction = findViewById(R.id.buttonAction);

                if (position == items.size() - 1) {
                    buttonAction.setText(R.string.mulai_sekarang);
                    buttonAction.setIcon(null);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) buttonAction.getLayoutParams();
                    params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                    params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

                    int marginPx = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
                    params.setMargins(marginPx, params.topMargin, marginPx, params.bottomMargin);

                    buttonAction.setLayoutParams(params);

                    buttonAction.setGravity(Gravity.CENTER);

                    textSkip.setVisibility(View.INVISIBLE);
                } else {
                    buttonAction.setText(R.string.next);
                    buttonAction.setIcon(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.ic_arrow_forward));

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) buttonAction.getLayoutParams();
                    params.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET;
                    params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

                    int marginEndPx = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                    params.setMargins(0, params.topMargin, marginEndPx, params.bottomMargin);

                    buttonAction.setLayoutParams(params);

                    buttonAction.setGravity(Gravity.CENTER_VERTICAL);
                    textSkip.setVisibility(View.VISIBLE);
                }
            }
        });

        textTitle.setText(items.get(0).getTitle());
        textDescription.setText(items.get(0).getDescription());
        ImageView firstIndicator = (ImageView) layoutOnboardingIndicators.getChildAt(0);
        if(firstIndicator != null) firstIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));

        textSkip.setOnClickListener(v -> navigateTo(LoginActivity.class));
        buttonAction.setOnClickListener(v -> {
            if (tutorialViewPager.getCurrentItem() + 1 < tutorialAdapter.getItemCount()) {
                tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem() + 1);
            } else {
                navigateTo(LoginActivity.class);
            }
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}