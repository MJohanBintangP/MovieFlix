package com.telkomsby.movieflix.model;

public class RegularUser extends User {

    private boolean isSubscribed;
    private String subscriptionExpiry;

    public RegularUser(String userId, String email, String name, String profilePicture) {
        super(userId, email, name, profilePicture);
        this.isSubscribed = false;
        this.subscriptionExpiry = "";
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public String getSubscriptionExpiry() {
        return subscriptionExpiry;
    }

    @Override
    public void displayProfile() {
        System.out.println("User Profile: " + name + " - " + email);
    }
}