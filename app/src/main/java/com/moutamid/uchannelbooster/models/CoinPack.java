package com.moutamid.uchannelbooster.models;

public class CoinPack {
    private String id;
    private int coins;
    private double price;

    public CoinPack() {
        // Default constructor required for Firebase
    }

    public CoinPack(String id, int coins, double price) {
        this.id = id;
        this.coins = coins;
        this.price = price;
    }

    public String getId() { return id; }
    public int getCoins() { return coins; }
    public double getPrice() { return price; }
}
