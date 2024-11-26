package com.example.clothesshoppingapp.models;

import java.util.List;

public class Product {
    private String name;
    private String description;
    private int price;
    private int originalPrice;
    private String discount;
    private float rating;
    private int reviews;
    private String imageUrl;
    private List<String> size;

    public Product() {

    }

    public Product(String name, String description, int price, int originalPrice, String discount, float rating, int reviews, String imageUrl, List<String> size) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.rating = rating;
        this.reviews = reviews;
        this.imageUrl = imageUrl;
        this.size = size;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getOriginalPrice() { return originalPrice; }
    public String getDiscount() { return discount; }
    public float getRating() { return rating; }
    public int getReviews() { return reviews; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getSize() { return size; }
}
