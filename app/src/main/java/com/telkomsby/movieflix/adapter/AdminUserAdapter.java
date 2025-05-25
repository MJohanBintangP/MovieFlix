package com.telkomsby.movieflix.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.telkomsby.movieflix.R;
import com.telkomsby.movieflix.model.UserItem;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder> {

    private final List<UserItem> userList;
    private final OnUserClickListener onUserClickListener;

    public interface OnUserClickListener {
        void onUserClick(UserItem user);
    }

    public AdminUserAdapter(List<UserItem> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.onUserClickListener = listener;
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        UserItem user = userList.get(position);
        holder.bind(user);

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null && position != RecyclerView.NO_POSITION) {
                onUserClickListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class AdminUserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
        }

        public void bind(UserItem user) {
            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail());
        }
    }
}