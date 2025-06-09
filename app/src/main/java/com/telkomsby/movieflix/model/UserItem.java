package com.telkomsby.movieflix.model;

public class UserItem {
    private String userId;
    private String name;
    private String email;
    private boolean isBanned;
    private String role;

    public UserItem(String userId, String name, String email, boolean isBanned, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.isBanned = isBanned;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBanned(boolean banned) {
        this.isBanned = banned;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
