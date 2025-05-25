package com.telkomsby.movieflix;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.adapter.AdminUserAdapter;
import com.telkomsby.movieflix.model.UserItem;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AdminUserAdapter adapter;
    private List<UserItem> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(userList, this::handleUserClick);
        rvUsers.setAdapter(adapter);

        loadUsersList();
    }

    private void loadUsersList() {
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getId();
                        String name = document.getString("name");
                        String email = document.getString("email");
                        Boolean isBanned = document.getBoolean("isBanned");
                        String role = document.getString("role");

                        UserItem userItem = new UserItem(userId, name, email, isBanned, role);
                        userList.add(userItem);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void handleUserClick(UserItem user) {
        if ("admin".equals(user.getRole())) {
            Toast.makeText(this, "Admin tidak bisa dibanned", Toast.LENGTH_SHORT).show();
            return;
        }

        showBanDialog(user.getUserId(), user.isBanned());
    }

    private void showBanDialog(String userId, boolean isBanned) {
        String title = isBanned ? "Unban Pengguna?" : "Ban Pengguna?";
        String message = isBanned ? "Yakin ingin mengaktifkan akun ini?" : "Yakin ingin mem-banned pengguna ini?";

        new androidx.appcompat.app.AlertDialog.Builder(AdminActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (isBanned) {
                        unbanUser(userId);
                    } else {
                        banUser(userId);
                    }
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void banUser(String userId) {
        db.collection("users").document(userId).update("isBanned", true)
                .addOnSuccessListener(aVoid -> Toast.makeText(AdminActivity.this, "User dibanned", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Gagal membanned", Toast.LENGTH_SHORT).show());
    }

    private void unbanUser(String userId) {
        db.collection("users").document(userId).update("isBanned", false)
                .addOnSuccessListener(aVoid -> Toast.makeText(AdminActivity.this, "User di-unban", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Gagal menghapus banned", Toast.LENGTH_SHORT).show());
    }
}