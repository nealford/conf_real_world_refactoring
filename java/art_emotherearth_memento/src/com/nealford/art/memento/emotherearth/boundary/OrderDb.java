package com.nealford.art.memento.emotherearth.boundary;

import com.nealford.art.memento.emotherearth.entity.CartItem;
import com.nealford.art.memento.emotherearth.entity.Order;
import com.nealford.art.memento.emotherearth.util.ShoppingCart;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class OrderDb {
    private static final String SQL_INSERT_LINEITEM =
            "INSERT INTO LINEITEMS (ORDER_KEY, ITEM_ID, QUANTITY)" +
                    " VALUES(?, ?, ?)";
    private static final String SQL_GET_USER_KEY =
            "SELECT ID FROM USERS WHERE NAME = ?";
    private static final String SQL_INSERT_ORDER =
        "INSERT INTO ORDERS (USER_KEY, CC_TYPE, CC_NUM, CC_EXP) " +
        "VALUES (?, ?, ?, ?)";
    private com.nealford.art.memento.emotherearth.util.DBPool dbPool;
    private Connection connection;
    private Statement stmt;

    public OrderDb() {
        
    }

    private void commitOrder(final Order order, int orderKey) throws SQLException {
        connection.commit();
        order.setOrderKeyFrom(orderKey);
    }

    public void addOrder(final ShoppingCart cart, String userName,
                         Order order) throws SQLException {
        connection = null;
        stmt = null;
        try {
            connection = dbPool.getConnection();
            insertOrder(getUserKey(userName), order);
            int orderKey = getGeneratedOrderKey();
            insertLineItems(cart, orderKey);
            commitOrder(order, orderKey);
        } catch (SQLException sqlx) {
            connection.rollback();
            throw sqlx;
        } finally {
            try {
                dbPool.release(connection);
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    private void insertLineItems(final ShoppingCart cart, int orderKey) throws SQLException {
        Iterator it = cart.getItemList().iterator();
        while (it.hasNext()) {
            CartItem ci = (CartItem) it.next();
            addLineItem(connection, orderKey, ci.getProduct().getId(),
                ci.getQuantity());
        }
    }

    private int getGeneratedOrderKey() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT IDENTITY_VAL_LOCAL() from ORDERS");
        int orderKey;
        if (rs.next()) {
            orderKey = rs.getInt(1);
        } else {
            throw new SQLException("Order.addOrderFrom(): no generated key");
        }
        return orderKey;
    }

    private void insertOrder(int userKey, Order order) throws SQLException {
        stmt = connection.createStatement();

        PreparedStatement ps = connection.prepareStatement(SQL_INSERT_ORDER);
        ps.setInt(1, userKey);
        ps.setString(2, order.getCcType());
        ps.setString(3, order.getCcNum());
        ps.setString(4, order.getCcExp());
        int result = ps.executeUpdate();
        if (result != 1) {
            throw new SQLException("Order.addOrderFrom(): order insert failed");
        }
    }

    private int getUserKey(String userName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SQL_GET_USER_KEY);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Order.addOrderFrom(): user not found");
        }
        return rs.getInt(1);
    }


    public void addLineItem(Connection c, int orderKey,
                            int itemId, int quantity) throws SQLException {
        PreparedStatement ps;
        ps = c.prepareStatement(SQL_INSERT_LINEITEM);
        ps.setInt(1, orderKey);
        ps.setInt(2, itemId);
        ps.setInt(3, quantity);
        int result = ps.executeUpdate();
        if (result != 1) {
            throw new SQLException("Lineitem.addLineItem(): insert failed");
        }
    }
    public void setDbPool(com.nealford.art.memento.emotherearth.util.DBPool dbPool) {
        this.dbPool = dbPool;
    }
}