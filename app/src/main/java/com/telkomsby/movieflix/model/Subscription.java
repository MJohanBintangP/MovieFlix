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

    public boolean createSubscription() {
        // Simulasi pembuatan langganan
        return true;
    }

    public boolean cancelSubscription() {
        // Simulasi pembatalan langganan
        return true;
    }

    public double calculatePriceWithTax() {
        // Hitung harga dengan pajak
        return price * 1.1; // Contoh pajak 10%
    }

    public boolean isUserAllowed(int userId) {
        // Cek apakah pengguna memiliki akses
        return this.userId == userId;
    }
}
