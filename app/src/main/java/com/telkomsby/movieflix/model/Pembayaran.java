package com.telkomsby.movieflix.model;

public class Pembayaran {
    private int pembayaranId;
    private String namaMetode;
    private int transaksiId;

    public Pembayaran(int pembayaranId, String namaMetode, int transaksiId) {
        this.pembayaranId = pembayaranId;
        this.namaMetode = namaMetode;
        this.transaksiId = transaksiId;
    }

    public boolean processPayment() {
        // Simulasi proses pembayaran
        return true;
    }

    public String getPaymentInfo() {
        return "Metode Pembayaran: " + namaMetode;
    }

    public String makePayment() {
        // Simulasi pembuatan transaksi pembayaran
        return "Pembayaran berhasil";
    }

    public String cancelPayment() {
        // Simulasi pembatalan pembayaran
        return "Pembayaran dibatalkan";
    }
}