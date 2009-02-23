package com.nealford.art.memento.emotherearth.entity;

import java.io.*;
import java.text.NumberFormat;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private static final NumberFormat formatter =
            NumberFormat.getCurrencyInstance();

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getExtendedPrice() {
        return product.getPrice() * quantity;
    }

    public String getExtendedPriceAsCurrency() {
        return formatter.format(getExtendedPrice());
    }


}