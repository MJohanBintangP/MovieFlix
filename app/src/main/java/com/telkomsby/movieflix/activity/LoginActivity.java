package com.telkomsby.movieflix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telkomsby.movieflix.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User is already logged in. Checking user status...");
            findViewById(android.R.id.content).setVisibility(View.INVISIBLE);
            checkUserStatusAndRedirect(currentUser);
        }

        MaterialButton buttonGoogleSignIn = findViewById(R.id.buttonGoogleSignIn);
        buttonGoogleSignIn.setOnClickListener(v -> {
            v.setEnabled(false);
            signInWithCredentialManager();
        });
    }

    private void signInWithCredentialManager() {
        GetSignInWithGoogleOption signInWithGoogleOption = new GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id)).build();
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(signInWithGoogleOption).build();

        credentialManager.getCredentialAsync(this, request, new CancellationSignal(), Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(@NonNull GetCredentialResponse result) {
                        try {
                            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Gagal memproses kredensial.", Toast.LENGTH_SHORT).show();
                                findViewById(R.id.buttonGoogleSignIn).setEnabled(true);
                            });
                        }
                    }
                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login dibatalkan.", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.buttonGoogleSignIn).setEnabled(true);
                        });
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        saveOrUpdateUserInfo(Objects.requireNonNull(task.getResult().getUser()));
                    } else {
                        Toast.makeText(this, "Autentikasi Firebase gagal.", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.buttonGoogleSignIn).setEnabled(true);
                    }
                });
    }

    private void saveOrUpdateUserInfo(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().exists()) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userId", user.getUid());
                userInfo.put("name", user.getDisplayName());
                userInfo.put("email", user.getEmail());
                userInfo.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                userInfo.put("role", "user");
                userInfo.put("isSubscribed", false);
                userInfo.put("subscriptionExpiry", null);
                userRef.set(userInfo).addOnCompleteListener(setTask -> checkUserStatusAndRedirect(user));
            } else {
                checkUserStatusAndRedirect(user);
            }
        });
    }

    private void checkUserStatusAndRedirect(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Boolean isBanned = documentSnapshot.getBoolean("isBanned");

                        if ("admin".equals(role)) {
                            Log.d(TAG, "User is admin. Navigating to AdminActivity.");
                            navigateTo(AdminActivity.class);
                            return;
                        }

                        if (Boolean.TRUE.equals(isBanned)) {
                            Toast.makeText(this, "Akun Anda telah diblokir.", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();

                            navigateTo(BannedActivity.class);
                            finish();
                            return;
                        }

                        Boolean isSubscribed = documentSnapshot.getBoolean("isSubscribed");
                        String subscriptionExpiryStr = documentSnapshot.getString("subscriptionExpiry");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date expiryDate = null;

                        try {
                            if (subscriptionExpiryStr != null && !subscriptionExpiryStr.isEmpty()) {
                                expiryDate = sdf.parse(subscriptionExpiryStr);
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "Gagal parse tanggal langganan", e);
                        }

                        boolean hasActiveSubscription = false;
                        if (isSubscribed != null && isSubscribed && expiryDate != null) {
                            hasActiveSubscription = expiryDate.after(new Date());
                        }

                        if (hasActiveSubscription) {
                            navigateTo(MainActivity.class);
                        } else {
                            navigateTo(PaymentActivity.class);
                        }

                    } else {
                        Log.w(TAG, "User document does not exist in Firestore. Navigating to PaymentActivity.");
                        navigateTo(PaymentActivity.class);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memverifikasi status akun.", Toast.LENGTH_LONG).show();
                    findViewById(android.R.id.content).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonGoogleSignIn).setEnabled(true);
                });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}