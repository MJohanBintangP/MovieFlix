package com.telkomsby.movieflix.model;

public class UserItem {
    private String userId;
    private String name;
    private String email;
    private boolean isBanned;
    private String role;

    public UserItem(String userId, String name, String email, Boolean isBanned, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.isBanned = isBanned != null && isBanned;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public String getRole() {
        return role;
    }
}