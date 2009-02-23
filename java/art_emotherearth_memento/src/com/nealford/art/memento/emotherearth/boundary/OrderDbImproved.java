package com.nealford.art.memento.emotherearth.boundary;

import com.nealford.art.memento.emotherearth.entity.CartItem;
import com.nealford.art.memento.emotherearth.entity.Order;
import com.nealford.art.memento.emotherearth.util.DBPool;
import com.nealford.art.memento.emotherearth.util.ShoppingCart;

import java.sql.*;
import java.util.Iterator;

/**
 * User: Neal Ford
 * Date: Jun 28, 2007
 * Time: 11:16:31 AM
 * <cite>Incidentally, created by IntelliJ IDEA.</cite>
 */
public class OrderDbImproved {
    private static final String SQL_INSERT_LINEITEM =
            "INSERT INTO LINEITEMS (ORDER_KEY, ITEM_ID, QUANTITY)" +
                    " VALUES(?, ?, ?)";
    private static final String TO_RETRIEVE_USER_KEY =
            "SELECT ID FROM USERS WHERE NAME = ?";
    private static final String SQL_INSERT_ORDER =
            "INSERT INTO ORDERS (USER_KEY, CC_TYPE, CC_NUM, CC_EXP)"
                    + "VALUES (?, ?, ?, ?)";
    private DBPool dbPool;

    public void addOrder(ShoppingCart cart, String userName,
                         Order order) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        Statement statement = null;
        ResultSet rs = null;
        boolean transactionState = false;
        try {
            connection = dbPool.getConnection();
            statement = connection.createStatement();
            transactionState =
                    setupTransactionStateFor(connection,
                            transactionState);
            addSingleOrder(order, connection,
                    ps, userKeyFor(userName, connection));
            order.setOrderKeyFrom(generateOrderKey(statement, rs));
            addLineItems(cart, connection, order.getOrderKey());
            completeTransaction(connection);
        } catch (SQLException sqlx) {
            rollbackTransactionFor(connection);
            throw sqlx;
        } finally {
            cleanUpDatabaseResources(connection,
                    transactionState, statement, ps, rs);
        }
    }


    private void cleanUpDatabaseResources(Connection connection,
            boolean transactionState, Statement s,
            PreparedStatement ps, ResultSet rs) throws SQLException {
        connection.setAutoCommit(transactionState);
        dbPool.release(connection);
        if (s != null)
            s.close();
        if (ps != null)
            ps.close();
        if (rs != null)
            rs.close();
    }

    private void rollbackTransactionFor(Connection connection)
            throws SQLException {
        connection.rollback();
    }

    private void completeTransaction(Connection c)
            throws SQLException {
        c.commit();
    }

    private boolean setupTransactionStateFor(Connection c,
            boolean transactionState) throws SQLException {
        transactionState = c.getAutoCommit();
        c.setAutoCommit(false);
        return transactionState;
    }

    private void addLineItems(ShoppingCart cart, Connection c,
                              int orderKey) throws SQLException {
        Iterator it = cart.getItemList().iterator();
        while (it.hasNext()) {
            CartItem ci = (CartItem) it.next();
            addLineItem(c, orderKey, ci.getProduct().getId(),
                    ci.getQuantity());
        }
    }

    private int generateOrderKey(Statement s, ResultSet rs) throws
            SQLException {
        rs = s.executeQuery("SELECT LAST_INSERT_ID()");
        int orderKey = -1;
        if (rs.next())
            orderKey = rs.getInt(1);
        else
            throw new SQLException(
                    "Order.addOrderFrom(): no generated key");
        return orderKey;
    }

    private void addSingleOrder(Order order, Connection c,
                                PreparedStatement ps,
                                int userKey) throws
            SQLException {
        ps = c.prepareStatement(SQL_INSERT_ORDER);
        ps.setInt(1, userKey);
        ps.setString(2, order.getCcType());
        ps.setString(3, order.getCcNum());
        ps.setString(4, order.getCcExp());
        int result = ps.executeUpdate();
        if (result != 1) {
            throw new SQLException(
                    "Order.addOrderFrom(): order insert failed");
        }
    }

    private int userKeyFor(String userName, Connection c)
            throws SQLException {
        PreparedStatement ps = c.prepareStatement(TO_RETRIEVE_USER_KEY);
        ResultSet rs;
        try {
            ps.setString(1, userName);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException(
                        "Order.addOrderFrom(): user not found");
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
