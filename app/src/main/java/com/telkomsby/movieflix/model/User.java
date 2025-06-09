package com.telkomsby.movieflix.model;

public abstract class User {
    protected String userId;
    protected String email;
    protected String name;
    protected String profilePicture;
    protected boolean isLoggedIn;

    public User(String userId, String email, String name, String profilePicture) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.profilePicture = profilePicture;
        this.isLoggedIn = false;
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

    public void login() {
        this.isLoggedIn = true;
    }

    public void logout() {
        this.isLoggedIn = false;
    }

    public abstract void displayProfile();
}