package com.telkomsby.movieflix.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.squareup.picasso.Picasso;
import com.telkomsby.movieflix.activity.LoginActivity;
import com.telkomsby.movieflix.R;

public class ProfileFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvUserName = view.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = view.findViewById(R.id.tv_user_email);
        TextView tvValidUntil = view.findViewById(R.id.tv_valid_until);
        ImageView ivProfilePhoto = view.findViewById(R.id.iv_profile_photo);
        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();

            tvUserName.setText(name);
            tvUserEmail.setMaxLines(1);
            tvUserEmail.setEllipsize(TextUtils.TruncateAt.END);
            tvUserEmail.setText(email);


            db.collection("users").document(firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String subscriptionExpiry = documentSnapshot.getString("subscriptionExpiry");
                            tvValidUntil.setText("Valid until " + subscriptionExpiry);
                        } else {
                            tvValidUntil.setText("Subscription expiry not found.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileFragment", "Error fetching subscription expiry", e);
                        tvValidUntil.setText("Failed to load expiry date.");
                    });

            if (firebaseUser.getPhotoUrl() != null) {
                Picasso.get()
                        .load(firebaseUser.getPhotoUrl())
                        .into(ivProfilePhoto);
            } else {
                ivProfilePhoto.setImageResource(R.drawable.ic_profile);
            }
        }

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            getActivity().finish();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });
    }
}