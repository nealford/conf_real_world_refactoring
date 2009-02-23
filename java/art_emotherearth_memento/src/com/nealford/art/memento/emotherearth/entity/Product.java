package com.nealford.art.memento.emotherearth.entity;

import java.io.*;
import java.text.*;

public class Product implements Serializable {
    private String name;
    private double price;
    private static final NumberFormat formatter =
            NumberFormat.getCurrencyInstance();
    private int id;

    public Product() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPriceAsCurrency() {
        return formatter.format(getPrice());
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}