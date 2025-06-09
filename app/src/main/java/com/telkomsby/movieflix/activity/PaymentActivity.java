package com.telkomsby.movieflix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telkomsby.movieflix.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        MaterialButton btnFreeTrial = findViewById(R.id.btn_free_trial);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Pengguna tidak terautentikasi. Silakan login kembali.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        btnFreeTrial.setOnClickListener(v -> processPayment(currentUser.getUid()));
    }

    private void processPayment(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> subscriptionData = new HashMap<>();
        subscriptionData.put("isSubscribed", true);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String expiryDate = dateFormat.format(calendar.getTime());

        subscriptionData.put("subscriptionExpiry", expiryDate);

        userRef.update(subscriptionData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PaymentActivity.this, "Pembayaran Berhasil! Langganan aktif.", Toast.LENGTH_SHORT).show();
                    navigateToSuccess();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PaymentActivity.this, "Gagal memperbarui status langganan.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating subscription", e);
                });
    }


    private void navigateToSuccess() {
        Intent intent = new Intent(PaymentActivity.this, SuccessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}