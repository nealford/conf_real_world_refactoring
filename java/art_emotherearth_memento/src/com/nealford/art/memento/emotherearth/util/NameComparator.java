package com.nealford.art.memento.emotherearth.util;

import java.util.Comparator;
import com.nealford.art.memento.emotherearth.entity.Product;

public class NameComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Product p1 = (Product) o1;
        Product p2 = (Product) o2;
        return p1.getName().compareTo(p2.getName());
    }

    public boolean equals(Object obj) {
        return this.equals(obj);
    }
}