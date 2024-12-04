package com.example.client;

public class Fruit {
    private String id;
    private String name;
    private String imageUrl;
    private String cost;
    private String color;
    private int quantity;
    public Fruit() {}
    public Fruit(String id, String name, String imageUrl, String cost, String color, int quantity) {}

    public Fruit(String name, String cost, int quantity, String imageUrl) {
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}



