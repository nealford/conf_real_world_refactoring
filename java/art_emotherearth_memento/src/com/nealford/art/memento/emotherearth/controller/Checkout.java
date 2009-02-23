package com.nealford.art.memento.emotherearth.controller;

import com.nealford.art.memento.emotherearth.boundary.OrderDb;
import com.nealford.art.memento.emotherearth.entity.Order;
import com.nealford.art.memento.emotherearth.util.DBPool;
import com.nealford.art.memento.emotherearth.util.ShoppingCart;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Checkout extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    /**Process the HTTP Post request*/
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //-- create dispatcher
        RequestDispatcher dispatcher = null;

        //--  check for session
        HttpSession session = request.getSession(false);
        if (session == null) {
            dispatcher = request.getRequestDispatcher("/welcome");
            dispatcher.forward(request, response);
            return;
        }

        //-- get information
        String user = (String) session.getAttribute("user");
        ShoppingCart sc = (ShoppingCart) session.getAttribute("cart");
        DBPool dbPool = (DBPool) getServletContext().getAttribute("dbPool");

        //-- insert order
        OrderDb orderDb = new OrderDb();
        orderDb.setDbPool(dbPool);

        Order order = new Order();
        order.setCcNum(request.getParameter("ccNum"));
        order.setCcExp(request.getParameter("ccExp"));
        order.setCcType(request.getParameter("ccType"));

        //-- validate parameters prior to update
        List errorList = order.validate();
        if (! errorList.isEmpty()) {
            request.setAttribute("errorList", errorList);
            dispatcher = request.getRequestDispatcher("/ShowCart");
            dispatcher.forward(request, response);
            return;
        }

        //-- insert the order
        try {
            orderDb.addOrder(sc, user, order);
        } catch (SQLException sqlx) {
            request.setAttribute(
                "javax.servlet.jsp.jspException", sqlx);
            dispatcher = request.getRequestDispatcher("/SQLErrorPage.jsp");
            dispatcher.forward(request, response);
            return;
        }

        //-- if you want to end the session...
        session.invalidate();

        //-- say goodbye to the user
        request.setAttribute("user", user);
        request.setAttribute("confirmation", new Integer(order.getOrderKey()));


        //-- forward to success view
        dispatcher = request.getRequestDispatcher("/CheckOutView.jsp");
        dispatcher.forward(request, response);
    }}