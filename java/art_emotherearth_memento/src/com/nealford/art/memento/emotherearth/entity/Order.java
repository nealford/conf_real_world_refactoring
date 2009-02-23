package com.nealford.art.memento.emotherearth.entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private int orderKey;
    private int userKey;
    private String orderStatus;
    private String shippingStatus;
    private String ccType;
    private String ccNum;
    private String ccExp;


    public List validate() {
        List errors = new ArrayList(0);
        if (!isValidCreditCardNumber())
            errors.add("Credit card number invalid");
        if (!isValidCreditCardExp())
            errors.add("No expiration data specfied");
        return errors;
    }

    public boolean isValidCreditCardNumber() {
        boolean bad = (ccNum == null || ccNum.equals(""));
        return !bad;
    }

    public boolean isValidCreditCardExp() {
        boolean bad = (ccExp == null || ccExp.equals(""));
        return !bad;
    }

    public String getCcExp() {
        return ccExp;
    }

    public String getCcNum() {
        return ccNum;
    }

    public String getCcType() {
        return ccType;
    }

    public int getOrderKey() {
        return orderKey;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public int getUserKey() {
        return userKey;
    }

    public void setUserKey(int userKey) {
        this.userKey = userKey;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderKeyFrom(int orderKey) {
        this.orderKey = orderKey;
    }

    public void setCcType(String ccType) {
        this.ccType = ccType;
    }

    public void setCcNum(String ccNum) {
        this.ccNum = ccNum;
    }

    public void setCcExp(String ccExp) {
        this.ccExp = ccExp;
    }
}