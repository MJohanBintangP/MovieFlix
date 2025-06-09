package com.telkomsby.movieflix.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.model.UserItem;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<UserItem> userList;
    private final FirebaseFirestore db;
    private final Context context;

    public UserAdapter(List<UserItem> userList, FirebaseFirestore db, Context context) {
        this.userList = userList;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItem item = userList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvEmail.setText(item.getEmail());

        String role = item.getRole();
        boolean isBanned = item.isBanned();

        if (isBanned) {
            holder.btnBan.setVisibility(View.GONE);
            holder.btnUnban.setVisibility(View.VISIBLE);
        } else if ("admin".equals(role)) {
            holder.btnBan.setVisibility(View.VISIBLE);
            holder.btnUnban.setVisibility(View.GONE);
        } else {
            holder.btnBan.setVisibility(View.VISIBLE);
            holder.btnUnban.setVisibility(View.GONE);
        }

        holder.btnBan.setOnClickListener(v -> {
            if ("admin".equals(item.getRole())) {
                Toast.makeText(context, "Admin tidak bisa dibanned", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users").document(item.getUserId())
                    .update("isBanned", true)
                    .addOnSuccessListener(aVoid -> {
                        item.setBanned(true);
                        notifyItemChanged(holder.getAdapterPosition());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Gagal membanning pengguna", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.btnUnban.setOnClickListener(v -> {
            db.collection("users").document(item.getUserId())
                    .update("isBanned", false)
                    .addOnSuccessListener(aVoid -> {
                        item.setBanned(false);
                        notifyItemChanged(holder.getAdapterPosition());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Gagal menghapus banned", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        Button btnBan, btnUnban;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            btnBan = itemView.findViewById(R.id.btn_ban);
            btnUnban = itemView.findViewById(R.id.btn_unban);
        }
    }
}