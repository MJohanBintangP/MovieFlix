package com.telkomsby.movieflix.model;

public class AdminUser extends User {
    private String adminCode;

    public AdminUser(int userId, String email, String name, String profilePicture, String adminCode) {
        super(userId, email, name, profilePicture);
        this.adminCode = adminCode;
    }

    @Override
    public void displayProfile() {
        System.out.println("Admin Profile: " + name + ", Email: " + email);
    }

    public void manageMovies() {
        // Implementasi manajemen film
    }

    public void banUser(User user) {
        // Implementasi banned user
    }
}