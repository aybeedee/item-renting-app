package com.abdullahumer.i200528;

public class Item {

    String itemId;
    String owner;
    String itemName;
    Double rate;
    String description;
    String city;
    String day;
    String month;
    String year;
    String itemImageUrl;
    String itemVideoUrl;

    public Item() {}

    public Item(String itemId, String owner, String itemName, Double rate, String description, String city, String day, String month, String year, String itemImageUrl, String itemVideoUrl) {
        this.itemId = itemId;
        this.owner = owner;
        this.itemName = itemName;
        this.rate = rate;
        this.description = description;
        this.city = city;
        this.day = day;
        this.month = month;
        this.year = year;
        this.itemImageUrl = itemImageUrl;
        this.itemVideoUrl = itemVideoUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public String getItemVideoUrl() {
        return itemVideoUrl;
    }

    public void setItemVideoUrl(String itemVideoUrl) {
        this.itemVideoUrl = itemVideoUrl;
    }
}
