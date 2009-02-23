package com.nealford.art.memento.emotherearth.util;

import com.nealford.art.memento.emotherearth.entity.CartItem;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.*;

public class ShoppingCart implements Serializable {
    private List itemList;
    private static final NumberFormat formatter =
            NumberFormat.getCurrencyInstance();

    public ShoppingCart() {
        itemList = new ArrayList(5);
    }

    public void addItem(final CartItem ci) {
        itemList.add(ci);
    }

    public double getCartTotal() {
        Iterator it = itemList.iterator();
        double sum = 0;
        while (it.hasNext())
            sum += ((CartItem) it.next()).getExtendedPrice();
        return sum;
    }

    public String getTotalAsCurrency() {
        return formatter.format(getCartTotal());
    }

    public java.util.List getItemList() {
        return itemList;
    }

    public ShoppingCartMemento setBookmark() {
        ShoppingCartMemento memento = new ShoppingCartMemento();
        memento.saveMemento();
        return memento;
        
    }

    public void restoreFromBookmark(ShoppingCartMemento memento) {
        this.itemList = memento.restoreMemento();
    }

    public class ShoppingCartMemento {
        private List itemList;

        public List restoreMemento() {
            return itemList;
        }

        public void saveMemento() {
            itemList = new ArrayList(itemList.size());
            Iterator i = itemList.iterator();
            while (i.hasNext())
                itemList.add(i.next());
        }
    }

}