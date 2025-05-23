package com.telkomsby.movieflix.model;

public class RegularUser extends User {
    private String googleId;

    public RegularUser(int userId, String email, String name, String profilePicture, String googleId) {
        super(userId, email, name, profilePicture);
        this.googleId = googleId;
    }

    @Override
    public void displayProfile() {
        System.out.println("Profile: " + name + ", Email: " + email);
    }

    public boolean loginWithGoogle(String googleToken) {
        // Simulasi login dengan Google
        if (googleToken != null && !googleToken.isEmpty()) {
            login();
            return true;
        }
        return false;
    }
}