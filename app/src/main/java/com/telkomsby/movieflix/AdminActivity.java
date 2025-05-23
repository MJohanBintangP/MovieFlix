package com.telkomsby.movieflix;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnPromoteUser = findViewById(R.id.btn_promote_user);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnPromoteUser.setOnClickListener(v -> {
            String userId = "USER_ID_YANG_INGIN_DIPROMOSIKAN"; // Ganti sesuai logic atau input
            promoteUserToAdmin(db, userId);
        });
    }

    private void promoteUserToAdmin(FirebaseFirestore db, String userId) {
        db.collection("users").document(userId)
                .update("role", "admin")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User berhasil dipromosikan menjadi admin", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Role updated to admin for user ID: " + userId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal mempromosikan user", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating role to admin", e);
                });
    }
}
