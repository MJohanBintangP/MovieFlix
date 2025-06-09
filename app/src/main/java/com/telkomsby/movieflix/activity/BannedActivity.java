package com.telkomsby.movieflix.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.telkomsby.movieflix.R;
import com.google.firebase.auth.FirebaseAuth;

public class BannedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned);

        findViewById(R.id.btn_logout_banned).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}