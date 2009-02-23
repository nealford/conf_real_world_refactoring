package com.nealford.art.memento.emotherearth.entity;

import java.io.*;

public class Lineitem implements Serializable {
    private int lineitemKey;
    private int orderKey;
    private int itemId;
    private int quantity;

    public Lineitem() {
    }

    public int getItemId() {
        return itemId;
    }

    public int getLineitemKey() {
        return lineitemKey;
    }

    public int getOrderKey() {
        return orderKey;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrderKey(int orderKey) {
        this.orderKey = orderKey;
    }

    public void setLineitemKey(int lineitemKey) {
        this.lineitemKey = lineitemKey;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}