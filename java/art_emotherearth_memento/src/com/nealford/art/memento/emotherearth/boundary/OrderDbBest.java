package com.nealford.art.memento.emotherearth.boundary;

import com.nealford.art.memento.emotherearth.entity.CartItem;
import com.nealford.art.memento.emotherearth.entity.Order;
import com.nealford.art.memento.emotherearth.util.DBPool;
import com.nealford.art.memento.emotherearth.util.ShoppingCart;

import java.sql.*;
import java.util.*;

/**
 * User: Neal Ford
 * Date: Jun 28, 2007
 * Time: 11:16:31 AM
 * <cite>Incidentally, created by IntelliJ IDEA.</cite>
 */
public class OrderDbBest {
    private static final String TO_RETRIEVE_USER_KEY =
            "SELECT ID FROM USERS WHERE NAME = ?";
    private static final String SQL_INSERT_LINEITEM =
            "INSERT INTO LINEITEMS (ORDER_KEY, ITEM_ID, QUANTITY)" +
                    " VALUES(?, ?, ?)";
    private static final String SQL_INSERT_ORDER =
            "INSERT INTO ORDERS (USER_KEY, CC_TYPE, CC_NUM, CC_EXP)"
                    + "VALUES (?, ?, ?, ?)";
    private DBPool dbPool;
    private Map _db;

    public void addOrderFrom(ShoppingCart cart, String userName,
                         Order order) throws SQLException {
        setupDataInfrastructure();
        try {
            add(order, userKeyBasedOn(userName));
            addLineItemsFrom(cart, order.getOrderKey());
            completeTransaction();
        } catch (SQLException sqlx) {
            rollbackTransaction();
            throw sqlx;
        } finally {
            cleanUp();
        }
    }

    private void setupDataInfrastructure() throws SQLException {
        _db = new HashMap();
        Connection c = dbPool.getConnection();
        _db.put("connection", c);
        _db.put("transaction state",
                Boolean.valueOf(setupTransactionStateFor(c)));
    }

    private void cleanUp() throws SQLException {
        Connection connection = (Connection) _db.get("connection");
        boolean transactionState = ((Boolean)
                _db.get("transation state")).booleanValue();
        Statement s = (Statement) _db.get("statement");
        PreparedStatement ps = (PreparedStatement)
                _db.get("prepared statement");
        ResultSet rs = (ResultSet) _db.get("result set");
        connection.setAutoCommit(transactionState);
        dbPool.release(connection);
        if (s != null)
            s.close();
        if (ps != null)
            ps.close();
        if (rs != null)
            rs.close();
    }

    private void rollbackTransaction()
            throws SQLException {
        ((Connection) _db.get("connection")).rollback();
    }

    private void completeTransaction()
            throws SQLException {
        ((Connection) _db.get("connection")).commit();
    }

    private boolean setupTransactionStateFor(Connection c)
            throws SQLException {
        boolean transactionState = c.getAutoCommit();
        c.setAutoCommit(false);
        return transactionState;
    }

    private void addLineItemsFrom(ShoppingCart cart, int orderKey
    ) throws SQLException {
        Map dbInfrastructure = _db;
        Connection c = (Connection) dbInfrastructure.get("connection");
        Iterator it = cart.getItemList().iterator();
        while (it.hasNext()) {
            CartItem ci = (CartItem) it.next();
            addLineItem(c, orderKey, ci.getProduct().getId(),
                    ci.getQuantity());
        }
    }

    private int generateOrderKeyFrom(Map dbInfrastructure) throws
            SQLException {
        Statement s = (Statement) dbInfrastructure.get("statement");
        ResultSet rs = s.executeQuery("SELECT LAST_INSERT_ID()");
        int orderKey = -1;
        if (rs.next())
            orderKey = rs.getInt(1);
        else
            throw new SQLException(
                    "Order.add(): no generated key");
        dbInfrastructure.put("result set", rs);
        return orderKey;
    }

    private void add(Order order, int userKey) throws
            SQLException {
        Map dbInfrastructure = _db;
        Connection c = (Connection) dbInfrastructure.get("connection");
        PreparedStatement ps = c.prepareStatement(SQL_INSERT_ORDER);
        ps.setInt(1, userKey);
        ps.setString(2, order.getCcType());
        ps.setString(3, order.getCcNum());
        ps.setString(4, order.getCcExp());
        int result = ps.executeUpdate();
        if (result != 1) {
            throw new SQLException(
                    "Order.add(): order insert failed");
        }
        dbInfrastructure.put("prepared statement", ps);
        order.setOrderKeyFrom(generateOrderKeyFrom(dbInfrastructure));
    }

    private int userKeyBasedOn(String userName)
            throws SQLException {
        Map dbInfrastructure = _db;
        Connection c = (Connection) dbInfrastructure.get("connection");
        PreparedStatement ps = c.prepareStatement(TO_RETRIEVE_USER_KEY);
        ResultSet rs;
        try {
            ps.setString(1, userName);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException(
                        "Order.add(): user not found");
            }
            int userKey = rs.getInt(1);
            return userKey;
        } finally {
            ps.close();
        }
    }


    public void addLineItem(Connection c, int orderKey,
                            int itemId, int quantity) throws
            SQLException {
        PreparedStatement ps = null;
        ps = c.prepareStatement(SQL_INSERT_LINEITEM);
        ps.setInt(1, orderKey);
        ps.setInt(2, itemId);
        ps.setInt(3, quantity);
        int result = ps.executeUpdate();
        if (result != 1) {
            throw new SQLException(
                    "Lineitem.addLineItem(): insert failed");
        }
    }

    public void setDbPool(DBPool dbPool) {
        this.dbPool = dbPool;
    }

}
