package com.telkomsby.movieflix.model;

public abstract class User {
    protected int userId;
    protected String email;
    protected String name;
    protected String profilePicture;
    protected boolean isLoggedIn;

    public User(int userId, String email, String name, String profilePicture) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.profilePicture = profilePicture;
        this.isLoggedIn = false;
    }

    public void login() {
        isLoggedIn = true;
    }

    public void logout() {
        isLoggedIn = false;
    }

    public boolean isLoggedin() {
        return isLoggedIn;
    }

    public abstract void displayProfile();
}