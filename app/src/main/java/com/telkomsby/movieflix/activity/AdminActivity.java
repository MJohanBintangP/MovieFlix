package com.telkomsby.movieflix.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.adapter.UserAdapter;
import com.telkomsby.movieflix.model.UserItem;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserItem> userList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        userAdapter = new UserAdapter(userList, db, this);

        recyclerView.setAdapter(userAdapter);

        loadAllUsers();
    }

    private void loadAllUsers() {
        CollectionReference usersRef = db.collection("users");

        usersRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Gagal memuat daftar pengguna", Toast.LENGTH_SHORT).show();
                return;
            }

            userList.clear();
            for (QueryDocumentSnapshot doc : value) {
                String userId = doc.getId();
                String name = doc.getString("name");
                String email = doc.getString("email");
                Boolean isBanned = doc.getBoolean("isBanned");
                String role = doc.getString("role");

                userList.add(new UserItem(userId, name, email, isBanned != null && isBanned, role));
            }

            userAdapter.notifyDataSetChanged();
        });
    }
}