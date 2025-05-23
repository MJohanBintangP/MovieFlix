package com.telkomsby.movieflix;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private CredentialManager credentialManager;
    private Executor mainExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainExecutor = ContextCompat.getMainExecutor(this);

        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User already signed in: " + currentUser.getUid());
            navigateToMain();
            return;
        }

        MaterialButton buttonGoogleSignIn = findViewById(R.id.buttonGoogleSignIn);
        buttonGoogleSignIn.setOnClickListener(v -> signInWithCredentialManager());
    }


    private void signInWithCredentialManager() {
        String serverClientId = getString(R.string.default_web_client_id);
        if (serverClientId.equals("YOUR_WEB_CLIENT_ID") || serverClientId.isEmpty()) {
            Log.e(TAG, "default_web_client_id is not set correctly in strings.xml");
            Toast.makeText(this, "Konfigurasi Klien Web ID belum diatur.", Toast.LENGTH_LONG).show();
            return;
        }

        GetSignInWithGoogleOption signInWithGoogleOption = new GetSignInWithGoogleOption
                .Builder(serverClientId)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build();

        launchCredentialManager(request);
    }

    private void launchCredentialManager(GetCredentialRequest request) {
        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(@NonNull GetCredentialResponse result) {
                        mainExecutor.execute(() -> createGoogleIdToken(result.getCredential()));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "GetCredentialException: " + e.getMessage(), e);
                        mainExecutor.execute(() -> {
                            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                            Toast.makeText(LoginActivity.this, getString(R.string.failed_to_get_credentials, errorMessage), Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }

    private void createGoogleIdToken(Credential credential) {
        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;
            if (customCredential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                try {
                    Bundle credentialData = customCredential.getData();
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);
                    firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
                } catch (Exception e) {
                    Log.e(TAG, "Error creating GoogleIdTokenCredential: " + e.getMessage(), e);
                    Toast.makeText(this, "Error memproses kredensial Google.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "Credential is not of type Google ID! Type: " + customCredential.getType());
                Toast.makeText(this, getString(R.string.credential_not_google_id), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "Credential is not a CustomCredential! Type: " + credential.getClass().getName());
            Toast.makeText(this, getString(R.string.credential_not_custom), Toast.LENGTH_SHORT).show();
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null) {
            Log.e(TAG, "ID Token is null, cannot authenticate with Firebase.");
            Toast.makeText(this, getString(R.string.authentication_failed) + " (ID Token null)", Toast.LENGTH_SHORT).show();
            return;
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase signInWithCredential successful");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {

                            saveUserInfoToFirestore(user);

                            String displayName = user.getDisplayName() != null ? user.getDisplayName() : getString(R.string.default_user_name);
                            String welcomeMessage = String.format(getString(R.string.welcome_message), displayName);
                            Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show();
                            navigateToMain();
                        } else {
                            Log.w(TAG, "User is null after successful Firebase authentication");
                            Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Firebase signInWithCredential failed", task.getException());
                        Toast.makeText(this, getString(R.string.authentication_failed) + ": " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveUserInfoToFirestore(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String role = documentSnapshot.getString("role");
                    if (role == null || role.isEmpty()) {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("userId", userId);
                        userInfo.put("name", user.getDisplayName() != null ? user.getDisplayName() : "");
                        userInfo.put("email", user.getEmail() != null ? user.getEmail() : "");
                        if (user.getPhotoUrl() != null) {
                            userInfo.put("photoUrl", user.getPhotoUrl().toString());
                        }
                        userInfo.put("role", "user");

                        db.collection("users").document(userId).set(userInfo)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "User info with role saved to Firestore for UID: " + userId);
                                    navigateToNextActivity(userId, "user");
                                });
                    } else {
                        Log.d(TAG, "User already has a role: " + role);
                        navigateToNextActivity(userId, role);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user role from Firestore", e);
                    Toast.makeText(this, "Gagal memuat informasi akun", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToNextActivity(String userId, String role) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isSubscribed = documentSnapshot.getBoolean("isSubscribed");
                Date expiryDate = documentSnapshot.getDate("subscriptionExpiry");

                boolean hasActiveSubscription = isSubscribed != null && isSubscribed && expiryDate != null && !expiryDate.before(new Date());

                if ("admin".equals(role)) {
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                } else if (hasActiveSubscription) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, PaymentActivity.class));
                }
                finish();
            }
        });
    }

}