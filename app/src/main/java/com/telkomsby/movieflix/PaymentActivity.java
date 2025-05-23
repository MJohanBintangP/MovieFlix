package com.telkomsby.movieflix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Button btnPayNow = findViewById(R.id.btn_pay_now);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();

        btnPayNow.setOnClickListener(v -> {
            // Simulasi proses pembayaran
            processPayment(userId, db);
        });
    }

    private void processPayment(String userId, FirebaseFirestore db) {
        DocumentReference userRef = db.collection("users").document(userId);

        // Simulasi pembayaran sukses
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isSubscribed", true);

        // Langganan berakhir dalam 30 hari
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        updateData.put("subscriptionExpiry", calendar.getTime());

        userRef.update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PaymentActivity.this, "Pembayaran Berhasil!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PaymentActivity.this, "Gagal memperbarui langganan", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating subscription", e);
                });
    }

    private void navigateToMain() {
        try {
            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("navigateToMain", "Navigation failed", e);
            Toast.makeText(this, "Gagal membuka halaman utama", Toast.LENGTH_SHORT).show();
        }
    }
}