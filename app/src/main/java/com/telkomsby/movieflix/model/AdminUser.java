package com.telkomsby.movieflix.model;

public class AdminUser extends User {

    public AdminUser(String userId, String email, String name, String profilePicture) {
        super(userId, email, name, profilePicture);
    }

    @Override
    public void displayProfile() {
        System.out.println("Admin Profile: " + name + " - " + email + " [Admin]");
    }

    // Tambahkan fitur admin lainnya di sini
    public void manageMovies() {
        // Implementasi manajemen film
    }

    public void banUser(User user) {
        // Implementasi banned user
    }
}
