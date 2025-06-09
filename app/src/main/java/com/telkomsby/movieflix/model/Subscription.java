package com.telkomsby.movieflix.model;

public class Subscription {
    private int subscriptionId;
    private int userId;
    private int transaksiId;
    private float price;
    private int pembayaranId;

    public Subscription(int subscriptionId, int userId, int transaksiId, float price, int pembayaranId) {
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.transaksiId = transaksiId;
        this.price = price;
        this.pembayaranId = pembayaranId;
    }

    public boolean isUserAllowed(int userId) {
        return this.userId == userId;
    }
}
