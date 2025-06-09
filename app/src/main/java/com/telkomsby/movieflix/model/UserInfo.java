package com.telkomsby.movieflix.model;

public class UserInfo {
    private String userId;
    private String email;
    private String name;
    private String profilePicture;
    private boolean isLoggedIn;
    private boolean isBanned;
    private boolean isSubscribed;
    private String subscriptionExpiry;
    private String role;

    public UserInfo() {}

    public UserInfo(String userId, String email, String name, String profilePicture, String role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.profilePicture = profilePicture;
        this.role = role;
        this.isLoggedIn = false;
        this.isSubscribed = false;
        this.isBanned = false;
        this.subscriptionExpiry = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public String getRole() {
        return role;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public String getSubscriptionExpiry() {
        return subscriptionExpiry;
    }
}