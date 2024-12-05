package com.example.clothesshoppingapp.models;

public class CartItem {
    private String imageUrl;
    private String name;
    private int price;
    private int quantity;

    public CartItem(String imageUrl, String name, int price, int quantity) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
